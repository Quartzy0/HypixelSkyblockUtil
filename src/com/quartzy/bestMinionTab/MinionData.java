package com.quartzy.bestMinionTab;

import org.jetbrains.annotations.NotNull;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class MinionData implements Comparable{
    private double coinsPerHour;
    private double coinsPerDay;
    private String name;
    
    private String displayCPH;
    private String displayCPD;
    
    public MinionData(double coinsPerHour, String name){
        this.coinsPerHour = coinsPerHour;
        this.coinsPerDay = this.coinsPerHour*24;
        this.name = name;
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);
        this.displayCPD = df.format(this.coinsPerDay);
        this.displayCPH = df.format(this.coinsPerHour);
    }
    
    public double getCoinsPerDay(){
        return coinsPerDay;
    }
    
    public void setCoinsPerDay(double coinsPerDay){
        this.coinsPerDay = coinsPerDay;
    }
    
    public double getCoinsPerHour(){
        return coinsPerHour;
    }
    
    public void setCoinsPerHour(double coinsPerHour){
        this.coinsPerHour = coinsPerHour;
    }
    
    public String getName(){
        return name;
    }
    
    public void setName(String name){
        this.name = name;
    }
    
    public String getDisplayCPH(){
        return displayCPH;
    }
    
    public void setDisplayCPH(String displayCPH){
        this.displayCPH = displayCPH;
    }
    
    public String getDisplayCPD(){
        return displayCPD;
    }
    
    public void setDisplayCPD(String displayCPD){
        this.displayCPD = displayCPD;
    }
    
    @Override
    public int compareTo(@NotNull Object o){
        if(!(o instanceof MinionData))return 0;
        MinionData m = (MinionData) o;
        return Double.compare(m.coinsPerHour, this.coinsPerHour);
    }
    
    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        
        MinionData that = (MinionData) o;
        
        if(Double.compare(that.coinsPerHour, coinsPerHour) != 0) return false;
        return name != null ? name.equals(that.name) : that.name == null;
    }
    
    @Override
    public String toString(){
        return "MinionData{" +
                "coinsPerHour=" + coinsPerHour +
                ", name='" + name + '\'' +
                '}';
    }
    
    @Override
    public int hashCode(){
        int result;
        long temp;
        temp = Double.doubleToLongBits(coinsPerHour);
        result = (int) (temp ^ (temp >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
