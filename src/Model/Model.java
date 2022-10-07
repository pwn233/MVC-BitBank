/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.sql.ResultSet;
import javax.swing.JComboBox;

/**
 *
 * @author User
 */
public class Model {
    private double amount;
    private String status;
    private double accAmount;
    private final int adminBBID = 1;
    private String bitcoin;
    private String fiat;
    private String detail;
    private double beforeAccAmount;
    public void makeTrans(double amount, JComboBox JComboBox2) {
        this.amount = amount;
        String func = (String) JComboBox2.getSelectedItem();
        switch (func) {
            case "withdraw FIAT":
                getFIATAcc();
                if(accAmount > amount) {
                    withdraw("FIAT",amount);
                }
                break;
            case "deposit FIAT":
                getFIATAcc();
                deposit("FIAT", amount);
                break;
            case "withdraw BITCOIN":
                getBITCOINAcc();
                if(accAmount > amount) {
                    withdraw("BITCOIN",amount);
                }
                break;
            case "deposit BITCOIN":
                getBITCOINAcc();
                deposit("BITCOIN",amount);
                break;
            default:
                break;
        }
    }
    private void withdraw(String type, double amount) {
        accAmount = accAmount - amount;
        detail = "withdraw";
        updateACC(type);
        transLog(status);
    }
    private void deposit(String type, double amount) {
        accAmount = accAmount + amount;
        detail = "deposit";
        updateACC(type);
        transLog(status);
    }
    private void getFIATAcc() {
        String query ="";
        //query valid check in phpmyadmin already
        query = String.format("SELECT FIAT FROM ACCOUNT WHERE BBID = %1$d", adminBBID);
        try {
            DBConnect conn = new DBConnect();
            boolean result = conn.execute(query);
            ResultSet rs = conn.getResult(query);
            if (result) {
                status = "complete";
                rs.next();
                accAmount = rs.getDouble("FIAT");
                beforeAccAmount = accAmount;
            } else {
                status = "fail {getFIATAcc}";
            }
            conn.close();
        } catch (Exception ex) {
            status = "Error Update: " + ex;
        }
    }
    private void getBITCOINAcc() {
            String query ="";
            //query valid check in phpmyadmin already
            query = String.format("SELECT BITCOIN FROM ACCOUNT WHERE BBID = %1$d", adminBBID);
            try {
                DBConnect conn = new DBConnect();
                ResultSet rs = conn.getResult(query);
                boolean result = conn.execute(query);
                if (result) {
                    status = "complete";
                    rs.next();
                    accAmount = rs.getDouble("BITCOIN");
                    beforeAccAmount = accAmount;
                } else {
                    status = "fail {getBITCOINAcc}";
                }
                conn.close();
            } catch (Exception ex) {
                status = "Error Update: " + ex;
            }
        }

    public String updateACC(String type) {
        String query ="";
        //query valid check in phpmyadmin already
        if(type.equals("FIAT"))
            query = String.format("UPDATE ACCOUNT SET FIAT = '%1$f' WHERE BBID = '%2$d'", accAmount, adminBBID);
        else
            query = String.format("UPDATE ACCOUNT SET BITCOIN = '%1$f' WHERE BBID = '%2$d'", accAmount, adminBBID);
        try {
            DBConnect conn = new DBConnect();
            boolean result = conn.execute(query);
            System.out.println(result);
            if (result) {
                status = "complete";
            } else {
                status = "fail {updateACC}";
            }
            conn.close();
        } catch (Exception ex) {
            status = "Error Update: " + ex;
        }
        return status;
    }

    public String getFiat() {
        String query ="";
        //query valid check in phpmyadmin already
        query = String.format("SELECT FIAT FROM ACCOUNT WHERE BBID = %1$d", adminBBID);
        try {
            DBConnect conn = new DBConnect();
            ResultSet rs = conn.getResult(query);
            boolean result = conn.execute(query);
            if (result) {
                status = "complete";
                rs.next();
                fiat = Double.toString(rs.getDouble("FIAT"));
            } else {
                status = "fail {getFiat}";
            }
            conn.close();
        } catch (Exception ex) {
            status = "Error Update: " + ex;
        }
        return fiat;
    }

    public String getBitcoin() {
        String query ="";
        //query valid check in phpmyadmin already
        query = String.format("SELECT BITCOIN FROM ACCOUNT WHERE BBID = %1$d", adminBBID);
        try {
            DBConnect conn = new DBConnect();
            ResultSet rs = conn.getResult(query);
            boolean result = conn.execute(query);
            if (result) {
                status = "complete";
                rs.next();
                bitcoin = Double.toString(rs.getDouble("BITCOIN"));
            } else {
                status = "fail {getBitcoin}";
            }
            conn.close();
        } catch (Exception ex) {
            status = "Error Update: " + ex;
        }
        return bitcoin;
    }

    private void transLog(String s) {
        String query ="";
        String status ="";
        query = String.format("INSERT INTO TRANS_LOG(ID, DETAIL, STATUS, BEFORE_ACC, AMOUNT, AFTER_ACC)VALUES(null , '%1$s', '%2$s', '%3$f', '%4$f', '%5$f')", detail, s, beforeAccAmount, amount, accAmount);
        try {
            DBConnect conn = new DBConnect();
            boolean result = conn.execute(query);
            if (result) {
                status = "complete";
            } else {
                status = "fail";
            }
            conn.close();
        } catch (Exception ex) {
            status = "Error Update: " + ex;
        }
    }
    
}
