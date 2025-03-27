package com.ibexmc.domain.helper;

import java.util.HashMap;

import com.ibexmc.domain.Domain;
import org.bukkit.OfflinePlayer;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

public class VaultFunctions {

    private final Domain instance;

    public VaultFunctions(Domain instance) {
        this.instance = instance;
    }

    public VaultReturn getBalance(OfflinePlayer offlinePlayer) {
        /**
         * getBalance
         * Gets the balance of an Offline Player
         * Returns a VaultReturn object (success, message, balance)
         *
         * @param OfflinePlayer The Offline player to check
         *
         */
        if (!Domain.getInstance().getDependencies().hasVault()) {
            return new VaultReturn(false, "Vault is not available", new Double(0));
        }
        if (Domain.getInstance().getDependencies().getEconomy() == null) {
            return new VaultReturn(false, "Vault economy is not available", new Double(0));
        }
        VaultReturn returnValue = new VaultReturn(false, "Unable to get Vault data", new Double(0));
        if (Domain.getInstance().getDependencies().hasVault()) {
            Economy economy = Domain.getInstance().getDependencies().getEconomy();
            return new VaultReturn(true, "", economy.getBalance(offlinePlayer));
        }
        return returnValue;
    }
    public VaultReturn withdraw(OfflinePlayer offlinePlayer, double amount) {
        /**
         * withdraw
         * Withdraws funds from an Offline Player if they have
         * the funds available to cover the withdrawal
         * Returns a VaultReturn object (success, message, balance)
         *
         * @param OfflinePlayer The Offline player to withdraw from
         * @param double The amount to withdraw
         *
         */
        if (!Domain.getInstance().getDependencies().hasVault()) {
            return new VaultReturn(false, "Vault is not available", new Double(0));
        }
        if (Domain.getInstance().getDependencies().getEconomy() == null) {
            return new VaultReturn(false, "Vault economy is not available", new Double(0));
        }
        VaultReturn returnValue = new VaultReturn(false, "Unable to get Vault data", new Double(0));
        Economy economy = Domain.getInstance().getDependencies().getEconomy();
        double playerBalance = economy.getBalance(offlinePlayer);
        if (playerBalance >= amount) {
            EconomyResponse r = economy.withdrawPlayer(offlinePlayer, amount);
            if (r.transactionSuccess()) {
                returnValue = new VaultReturn(true, instance.getLocale().localeText("vault_funds_withdrawn", "Funds have been withdrawn", null), playerBalance - amount);
            } else {
                // Failed
                HashMap<String, String> placeHolder = new HashMap<String, String>();
                placeHolder.put("<%err%>", r.errorMessage);
                return new VaultReturn(false, instance.getLocale().localeText("vault_funds_withdraw_fail", "Unable to withdraw funds: <%err%>", placeHolder), playerBalance);
            }
        } else {
            // Insufficient funds
            return new VaultReturn(false, instance.getLocale().localeText("vault_funds_withdraw_insufficient", "Insufficient funds for withdrawl", null), playerBalance);
        }
        return returnValue;
    }
    public VaultReturn deposit(OfflinePlayer offlinePlayer, double amount) {
        /**
         * deposit
         * Deposits funds to an Offline Player
         * Returns a VaultReturn object (success, message, balance)
         *
         * @param OfflinePlayer The Offline player to deposit funds to
         * @param double The amount to deposit
         *
         */
        if (!Domain.getInstance().getDependencies().hasVault()) {
            return new VaultReturn(false, "Vault is not available", new Double(0));
        }
        if (Domain.getInstance().getDependencies().getEconomy() == null) {
            return new VaultReturn(false, "Vault economy is not available", new Double(0));
        }
        VaultReturn returnValue = new VaultReturn(false, "Unable to get Vault data", new Double(0));
        Economy economy = Domain.getInstance().getDependencies().getEconomy();
        double playerBalance = economy.getBalance(offlinePlayer);
        EconomyResponse r = economy.depositPlayer(offlinePlayer, amount);
        if (r.transactionSuccess()) {
            returnValue = new VaultReturn(true, instance.getLocale().localeText("vault_funds_deposited", "Funds have been deposited", null), playerBalance - amount);
        } else {
            // Failed
            HashMap<String, String> placeHolder = new HashMap<String, String>();
            placeHolder.put("<%err%>", r.errorMessage);
            return new VaultReturn(false, instance.getLocale().localeText("vault_funds_deposit_fail", "Unable to deposit funds: <%err%>", placeHolder), playerBalance);
        }
        return returnValue;
    }
    public static class VaultReturn {
        private boolean success;
        private String message;
        private double balance;
        private double recipientBalance;

        public VaultReturn() {
            this.success = false;
            this.message = "";
            this.balance = new Double(0);
            this.recipientBalance = new Double(0);
        }
        public VaultReturn(boolean success, String message, double balance) {
            this.success = success;
            this.message = message;
            this.balance = balance;
            this.recipientBalance = new Double(0);
        }
        public VaultReturn(boolean success, String message, double balance, double recipientBalance) {
            this.success = success;
            this.message = message;
            this.balance = balance;
            this.recipientBalance = recipientBalance;
        }

        public boolean isSuccess() {
            return this.success;
        }

        public String getMessage() {
            return message;
        }

        public double getBalance() {
            return this.balance;
        }

        public double getRecipientBalance() {
            return this.recipientBalance;
        }
    }
}
