package com.edoxile.bukkit.gatesandbridges.Exceptions;

/**
 * Created by IntelliJ IDEA.
 * User: Edoxile
 */
public class InsufficientMaterialsException extends Exception {
    private int amount;

    public InsufficientMaterialsException(int a){
        amount = a;
    }

    public int getAmount(){
        return amount;
    }

    public String toString(){
        return Integer.toString(amount);
    }
}
