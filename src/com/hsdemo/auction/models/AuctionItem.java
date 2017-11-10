package com.hsdemo.auction.models;

import android.app.Activity;

import com.hsdemo.auction.IdentityManager;
import com.google.api.client.util.Key;
import com.kinvey.java.LinkedResources.LinkedGenericJson;
import com.kinvey.java.model.KinveyMetaData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by jtsuji on 11/14/14.
 */

public class AuctionItem extends LinkedGenericJson {

  @Key("_id")
  private String entityId;
  @Key("name")
  private String name;
  @Key("price")
  private int price;
  @Key("priceIncrement")
  private int priceIncrement;
  @Key("currentPrice")
  private List<Integer> currentPrice;
  @Key("currentWinners")
  private List<String> currentWinners;
  @Key("allBidders")
  private List<String> allBidders;
  @Key("numberOfBids")
  private int numberOfBids;
  @Key("donorname")
  private String donorName;
  @Key("donorurl")
  private String donorUrl;
  @Key("imageurl")
  private String imageUrl;
  @Key("decription")
  private String itemDescription;
  @Key("qty")
  private int quantity;
  @Key("opentime")
  private String openTime;
  @Key("closetime")
  private String closeTime;
  @Key(KinveyMetaData.JSON_FIELD_NAME)
  private KinveyMetaData meta;

  private List<Bid> allBids;

  SimpleDateFormat kinveyDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");

  public AuctionItem() {}

  public String getEntityId() {
    return entityId;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return itemDescription.replaceAll("[\n]", "<br>");
  }

  public String getDonorName() {
    return donorName;
  }

  public String getDonorUrl() {
    return donorUrl;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public int getStartingPrice() {
    return price;
  }

  public int getPriceIncrement() {
    return priceIncrement;
  }

  public Date getOpenTime() {
    Date returnDate = new Date();
    try {
      returnDate = kinveyDateFormat.parse(openTime+"GMT");
    }
    catch (ParseException e) {
      e.printStackTrace();
    }
    return returnDate;
  }

  public Date getCloseTime() {
    Date returnDate = new Date();
    try {
      returnDate =  kinveyDateFormat.parse(closeTime+"GMT");
    }
    catch (ParseException e) {
      e.printStackTrace();
    }
    return returnDate;
  }

  public boolean isOpenForBidding() {
//    return !(getOpenTime().after(new Date()) || getCloseTime().before(new Date()));
    return (getOpenTime().before(new Date()) && getCloseTime().after(new Date()));
  }

  public List<Integer> getAllBids() {
    List<Integer> bids = getListOrEmptyList(currentPrice);
    Collections.sort(bids, new Comparator<Integer>() {
      @Override
      public int compare(Integer lhs, Integer rhs) {
        return rhs - lhs;
      }
    });

    return bids;
  }

	public List<Bid> getBids()
	{
		return allBids;
	}

	public void setBids(List<Bid> bids)
	{
		allBids = bids;
	}

  public List<String> getCurrentWinners() {
    return getListOrEmptyList(currentWinners);
  }

  public int getNumberOfBids() {
    return numberOfBids;
  }

  public int getQty() {
    return quantity;
  }

  public int getCurrentHighestBid() {
    return getAllBids().size() > 0 ? getAllBids().get(0) : getStartingPrice();
  }

  public int[] getLowHighWinningBid() {
    List<Integer> allBids = getAllBids();
    if (allBids.size() > 0) {
      return new int[]{allBids.get(allBids.size() - 1), allBids.get(0)};
    }
    else {
      return new int[]{getStartingPrice()};
    }
  }

  public List<String> getAllBidders() {
    return getListOrEmptyList(allBidders);
  }

  public boolean hasUserBid(Activity context) {
    return getAllBidders().contains(IdentityManager.getEmail(context));
  }

  public boolean isWinning(Activity context) {
    return getCurrentWinners().contains(IdentityManager.getEmail(context));
  }

  public int getMyBidWinningIdx(Activity context) {
    return getCurrentWinners().indexOf(IdentityManager.getEmail(context)) + 1;
  }

  public List getListOrEmptyList(List<?> list) {
    if (list.size() == 0)
      return new ArrayList();
    else
      return list;
  }
}
