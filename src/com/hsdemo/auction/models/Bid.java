package com.hsdemo.auction.models;

import com.google.api.client.util.Key;
import com.kinvey.java.LinkedResources.LinkedGenericJson;
import com.kinvey.java.model.KinveyMetaData;

import java.util.Date;

/**
 * Created by jtsuji on 11/14/14.
 */

public class Bid extends LinkedGenericJson {

  public static final String INITIAL_BIDDER_EMAIL = "";
  public static final String INITIAL_BIDDER_NAME = "Starting Price";

  public long createdAt = 0;

  @Key("_id")
  private String entityId;
  @Key("email")
  private String email;
  @Key("name")
  private String name;
  @Key("bidderNumber")
  private String bidderNumber;
  @Key("amt")
  private int amount;
  @Key("itemId")
  private String itemId;
  @Key(KinveyMetaData.JSON_FIELD_NAME)
  private KinveyMetaData meta;

  public Bid() {}

  public static Bid getInitialBid(int price) {
    Bid bid = new Bid();
    bid.setEmail(INITIAL_BIDDER_EMAIL);
    bid.setName(INITIAL_BIDDER_NAME);
    bid.setAmount(price);
    return bid;
  }

/*
  public Date getCreatedAtDate() {
//    return getCreatedAt() == null ? new Date(createdAt) : getCreatedAt();
    return new Date(createdAt);
  }
*/

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public int getAmount() {
    return amount;
  }

  public void setAmount(int amt) {
    this.amount = amt;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getBidderNumber() {
    return bidderNumber;
  }

  public void setBidderNumber(String bidderNumber) {
    this.bidderNumber = bidderNumber;
  }

  public String getRelatedItemId() {
    return itemId;
  }

  public void setRelatedItemId(String id) {
    this.itemId = id;
  }
}
