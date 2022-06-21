package com.cureforoptimism.lifebot.discord.command;

import static com.inamik.text.tables.Cell.Functions.HORIZONTAL_CENTER;
import static com.inamik.text.tables.Cell.Functions.RIGHT_ALIGN;

import com.cureforoptimism.lifebot.Utilities;
import com.cureforoptimism.lifebot.application.DiscordBot;
import com.cureforoptimism.lifebot.domain.SeedType;
import com.cureforoptimism.lifebot.service.MarketPriceMessageSubscriber;
import com.cureforoptimism.lifebot.service.SubgraphService;
import com.inamik.text.tables.SimpleTable;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.InteractionFollowupCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class FloorCommand implements DiscordCommand {
  private final DiscordBot discordBot;
  private final SubgraphService subgraphService;

  private final MarketPriceMessageSubscriber marketPriceMessageSubscriber;

  @Override
  public String getName() {
    return "floor";
  }

  @Override
  public String getDescription() {
    return "shows the current floor of all Seed of Life assets";
  }

  @Override
  public String getUsage() {
    return null;
  }

  @Override
  public Boolean adminOnly() {
    return false;
  }

  @Override
  public Mono<Message> handle(MessageCreateEvent event) {
    log.info("!floor command received");
    return event
        .getMessage()
        .getChannel()
        .flatMap(
            c -> {
              EmbedCreateSpec floorEmbed = getFloorEmbed();
              if (floorEmbed == null) {
                return Mono.empty();
              }

              return c.createMessage(MessageCreateSpec.builder().addEmbed(floorEmbed).build());
            });
  }

  @Override
  public Mono<Void> handle(ChatInputInteractionEvent event) {
    log.info("/floor command received");
    EmbedCreateSpec floorEmbed = getFloorEmbed();
    if (floorEmbed == null) {
      return Mono.empty();
    }

    event
        .deferReply()
        .then(
            event.createFollowup(
                InteractionFollowupCreateSpec.builder().addEmbed(floorEmbed).build()))
        .block();

    return Mono.empty();
  }

  private EmbedCreateSpec getFloorEmbed() {
    if (marketPriceMessageSubscriber.getLastMarketPlace() == null) {
      return null;
    }

    final Double ethMktPrice = marketPriceMessageSubscriber.getLastMarketPlace().getEthPrice();
    Double currentPrice = discordBot.getCurrentPrice();

    final BigDecimal seedOneMagicFloor = subgraphService.seedFloors.get(SeedType.SEED_1);
    final BigDecimal seedOneUsdFloor = seedOneMagicFloor.multiply(BigDecimal.valueOf(currentPrice));
    final BigDecimal seedTwoMagicFloor = subgraphService.seedFloors.get(SeedType.SEED_2);
    final BigDecimal seedTwoUsdFloor = seedTwoMagicFloor.multiply(BigDecimal.valueOf(currentPrice));

    final SimpleTable table =
        new SimpleTable()
            .nextRow()
            .nextCell("TYPE")
            .applyToCell(HORIZONTAL_CENTER.withWidth(12))
            .nextCell("MAGIC")
            .applyToCell(HORIZONTAL_CENTER.withWidth(7))
            .nextCell("USD")
            .applyToCell(HORIZONTAL_CENTER.withWidth(12))
            .nextCell("ETH")
            .applyToCell(HORIZONTAL_CENTER.withWidth(7));

    table
        .nextRow()
        .nextCell("SEED 1")
        .nextCell(String.format("%.00f", seedOneMagicFloor))
        .applyToCell(RIGHT_ALIGN.withWidth(7))
        .nextCell(String.format("$%.2f", seedOneUsdFloor))
        .applyToCell(RIGHT_ALIGN.withWidth(12))
        .nextCell(String.format("Ξ%.2f", seedOneUsdFloor.doubleValue() / ethMktPrice))
        .applyToCell(RIGHT_ALIGN.withWidth(7));

    table
        .nextRow()
        .nextCell("SEED 2")
        .nextCell(String.format("%.00f", seedTwoMagicFloor))
        .applyToCell(RIGHT_ALIGN.withWidth(7))
        .nextCell(String.format("$%.2f", seedTwoUsdFloor))
        .applyToCell(RIGHT_ALIGN.withWidth(12))
        .nextCell(String.format("Ξ%.2f", seedTwoUsdFloor.doubleValue() / ethMktPrice))
        .applyToCell(RIGHT_ALIGN.withWidth(7));

    final var output = "```\n" + Utilities.simpleTableToString(table) + "```\n";

    String title = "Seed of Life floor - Treasure/Trove Marketplace\nMAGIC: $" + currentPrice;
    title += "\nETH: $" + ethMktPrice;

    return EmbedCreateSpec.builder()
        .title(title)
        .author(
            "LifeBot",
            null,
            "https://pbs.twimg.com/profile_images/1531638907170967552/pSzO9Ivr_400x400.jpg")
        .description(output)
        .build();
  }
}
