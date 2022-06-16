package com.cureforoptimism.lifebot.service;

import com.cureforoptimism.lifebot.domain.SeedType;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SubgraphService {
  public final Map<SeedType, BigDecimal> seedFloors = new HashMap<>();

  @Scheduled(fixedDelay = 60000)
  public void updateFloors() {
    MathContext mc = new MathContext(10, RoundingMode.HALF_UP);

    // SOL1 floor query
    String jsonBody =
        "{\"query\":\"query getActivity($first: Int!, $skip: Int, $includeListings: Boolean!, $includeSales: Boolean!, $includeBids: Boolean!, $listingFilter: Listing_filter, $listingOrderBy: Listing_orderBy, $bidFilter: Bid_filter, $bidOrderBy: Bid_orderBy, $saleFilter: Sale_filter, $saleOrderBy: Sale_orderBy, $orderDirection: OrderDirection) {\\n  listings(\\n    first: $first\\n    where: $listingFilter\\n    orderBy: $listingOrderBy\\n    orderDirection: $orderDirection\\n    skip: $skip\\n  ) @include(if: $includeListings) {\\n    ...ListingFields\\n  }\\n  bids(\\n    first: $first\\n    where: $bidFilter\\n    orderBy: $bidOrderBy\\n    orderDirection: $orderDirection\\n    skip: $skip\\n  ) @include(if: $includeBids) {\\n    ...BidFields\\n  }\\n  sales(\\n    first: $first\\n    where: $saleFilter\\n    orderBy: $saleOrderBy\\n    orderDirection: $orderDirection\\n    skip: $skip\\n  ) @include(if: $includeSales) {\\n    ...SaleFields\\n  }\\n}\\n\\nfragment ListingFields on Listing {\\n  timestamp\\n  id\\n  pricePerItem\\n  quantity\\n  seller {\\n    id\\n  }\\n  token {\\n    id\\n    tokenId\\n  }\\n  collection {\\n    id\\n  }\\n  currency {\\n    id\\n  }\\n  status\\n  expiresAt\\n}\\n\\nfragment BidFields on Bid {\\n  timestamp\\n  id\\n  pricePerItem\\n  quantity\\n  token {\\n    id\\n    tokenId\\n  }\\n  collection {\\n    id\\n  }\\n  currency {\\n    id\\n  }\\n  buyer {\\n    id\\n  }\\n  status\\n  expiresAt\\n  bidType\\n}\\n\\nfragment SaleFields on Sale {\\n  timestamp\\n  id\\n  pricePerItem\\n  quantity\\n  type\\n  seller {\\n    id\\n  }\\n  buyer {\\n    id\\n  }\\n  token {\\n    id\\n    tokenId\\n  }\\n  collection {\\n    id\\n  }\\n  currency {\\n    id\\n  }\\n}\",\"variables\":{\"skip\":0,\"first\":1,\"listingOrderBy\":\"pricePerItem\",\"orderDirection\":\"asc\",\"listingFilter\":{\"collection\":\"0x3956c81a51feaed98d7a678d53f44b9166c8ed66\",\"status\":\"ACTIVE\",\"token\":\"0x3956c81a51feaed98d7a678d53f44b9166c8ed66-143\",\"expiresAt_gt\":1655353504},\"includeListings\":true,\"includeBids\":false,\"includeSales\":false},\"operationName\":\"getActivity\"}";

    HttpClient httpClient = HttpClient.newHttpClient();

    try {
      HttpRequest request =
          HttpRequest.newBuilder(
                  new URI(
                      "https://api.thegraph.com/subgraphs/name/vinnytreasure/treasuremarketplace-fast-prod"))
              .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
              .header("Content-Type", "application/json")
              .build();
      HttpResponse<String> response =
          httpClient.send(request, HttpResponse.BodyHandlers.ofString());
      JSONArray listings =
          new JSONObject(response.body()).getJSONObject("data").getJSONArray("listings");

      // This is ordered by price-per-item
      if (listings.length() != 0) {
        final var price = listings.getJSONObject(0).getBigInteger("pricePerItem");
        seedFloors.put(SeedType.SEED_1, new BigDecimal(price, 18, mc));
      }

      // SOL2 Floor query
      jsonBody =
          "{\"query\":\"query getActivity($first: Int!, $skip: Int, $includeListings: Boolean!, $includeSales: Boolean!, $includeBids: Boolean!, $listingFilter: Listing_filter, $listingOrderBy: Listing_orderBy, $bidFilter: Bid_filter, $bidOrderBy: Bid_orderBy, $saleFilter: Sale_filter, $saleOrderBy: Sale_orderBy, $orderDirection: OrderDirection) {\\n  listings(\\n    first: $first\\n    where: $listingFilter\\n    orderBy: $listingOrderBy\\n    orderDirection: $orderDirection\\n    skip: $skip\\n  ) @include(if: $includeListings) {\\n    ...ListingFields\\n  }\\n  bids(\\n    first: $first\\n    where: $bidFilter\\n    orderBy: $bidOrderBy\\n    orderDirection: $orderDirection\\n    skip: $skip\\n  ) @include(if: $includeBids) {\\n    ...BidFields\\n  }\\n  sales(\\n    first: $first\\n    where: $saleFilter\\n    orderBy: $saleOrderBy\\n    orderDirection: $orderDirection\\n    skip: $skip\\n  ) @include(if: $includeSales) {\\n    ...SaleFields\\n  }\\n}\\n\\nfragment ListingFields on Listing {\\n  timestamp\\n  id\\n  pricePerItem\\n  quantity\\n  seller {\\n    id\\n  }\\n  token {\\n    id\\n    tokenId\\n  }\\n  collection {\\n    id\\n  }\\n  currency {\\n    id\\n  }\\n  status\\n  expiresAt\\n}\\n\\nfragment BidFields on Bid {\\n  timestamp\\n  id\\n  pricePerItem\\n  quantity\\n  token {\\n    id\\n    tokenId\\n  }\\n  collection {\\n    id\\n  }\\n  currency {\\n    id\\n  }\\n  buyer {\\n    id\\n  }\\n  status\\n  expiresAt\\n  bidType\\n}\\n\\nfragment SaleFields on Sale {\\n  timestamp\\n  id\\n  pricePerItem\\n  quantity\\n  type\\n  seller {\\n    id\\n  }\\n  buyer {\\n    id\\n  }\\n  token {\\n    id\\n    tokenId\\n  }\\n  collection {\\n    id\\n  }\\n  currency {\\n    id\\n  }\\n}\",\"variables\":{\"skip\":0,\"first\":1,\"listingOrderBy\":\"pricePerItem\",\"orderDirection\":\"asc\",\"listingFilter\":{\"collection\":\"0x3956c81a51feaed98d7a678d53f44b9166c8ed66\",\"status\":\"ACTIVE\",\"token\":\"0x3956c81a51feaed98d7a678d53f44b9166c8ed66-142\",\"expiresAt_gt\":1655354584},\"includeListings\":true,\"includeBids\":false,\"includeSales\":false},\"operationName\":\"getActivity\"}";
      request =
          HttpRequest.newBuilder(
                  new URI(
                      "https://api.thegraph.com/subgraphs/name/vinnytreasure/treasuremarketplace-fast-prod"))
              .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
              .header("Content-Type", "application/json")
              .build();
      response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
      listings = new JSONObject(response.body()).getJSONObject("data").getJSONArray("listings");
      if (listings.length() != 0) {
        final var price = listings.getJSONObject(0).getBigInteger("pricePerItem");
        seedFloors.put(SeedType.SEED_2, new BigDecimal(price, 18, mc));
      }
    } catch (IOException | InterruptedException | URISyntaxException ex) {
      log.warn("Failed to retrieve treasure: ", ex);
    }
  }
}
