package logic;

import org.powbot.api.rt4.Inventory;

public class Alchable {
    private String name;
    private int buyLimit;
    private int highAlchPrice;
    private int id;
    private long lastBought;
    private int numberBought;
    private int tradedPrice;
    private long timestamp;

    Alchable(final String _name, final int _buyLimit, final int _highAlchPrice, final int _id, final long _timestamp)
    {
        name = _name;
        buyLimit = _buyLimit;
        highAlchPrice = _highAlchPrice;
        id = _id;
        timestamp = _timestamp;
    }

    public String getName()
    {
        return name;
    }

    public long getPotentialProfit()
    {
        if (getBuyLimit() <= 0)
            return 0;

        return getProfit();
    }

    public int getRealLimit()
    {
        return buyLimit;
    }

    public int getBuyLimit()
    {
        int numCoins = Inventory.stream().id(995).first().stackSize() - 130000;
        int price = getTradedPrice();
        if (numCoins <= 0 || price <= 0)
            return 0;
        int buyAmmount = (numCoins / price);
        int maxBuyAmmount = buyLimit - getNumberBought();
        if (buyAmmount > maxBuyAmmount)
            return maxBuyAmmount;

        if (buyAmmount < 0)
            buyAmmount = 0;
        return buyAmmount;
    }

    public int getHighAlchPrice()
    {
        return highAlchPrice;
    }

    public int getId()
    {
        return id;
    }

    public int getTradedPrice()
    {
        return tradedPrice;
    }

    public void setTradedPrice(int price){
        this.tradedPrice = price;
    }

    public int getProfit()
    {

        return getHighAlchPrice() - (205 + getTradedPrice());
    }

    public long getLastBought()
    {

        return lastBought;
    }
    public long getFailedTimeStamp(){
        return timestamp;
    }
    public void setFailedTimeStamp(Long timestamp){
        this.timestamp = timestamp;
    }

    public void setLastBought(long _lastBought)
    {
        lastBought = _lastBought;
    }

    public int getNumberBought()
    {
        if (getLastBought() + 14400000 < System.currentTimeMillis())
        {
            if (getLastBought() != 0)
                System.out.println(
                        name + " was last bought over 4 hours ago : " + this.getLastBought() + " - " + getLastBought());
            setLastBought(System.currentTimeMillis());
            setNumberBought(0);
        }

        return numberBought;
    }

    public void setNumberBought(int _numberBought)
    {
        numberBought = _numberBought;
    }

}

