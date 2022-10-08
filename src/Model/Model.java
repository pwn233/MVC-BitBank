/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.sql.ResultSet;
import javax.swing.JComboBox;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author User
 */
public class Model {
    private double amount;
    private String status;
    private double bcAmount;
    private double fiatAmount;
    private final int adminBBID = 1;
    private String bitcoin;
    private String fiat;
    private String detail;
    private double beforeAcc;
    private double afterAcc;
    public void makeTrans(double amount, JComboBox JComboBox2) {
        this.amount = amount;
        String func = (String) JComboBox2.getSelectedItem();
        String type;
        double x;
        switch (func) {
            case "withdraw FIAT":
                getFIATAcc();
                beforeAcc = fiatAmount;
                if(fiatAmount >= amount) {
                    fiatAmount = fiatAmount - amount;
                    afterAcc = fiatAmount;
                    detail = "withdraw";
                    type = "FIAT";
                    updateACC(type);
                    transLog(status, type, amount, beforeAcc, afterAcc);
                } else {
                    status = "not enough fiat in your balance.";
                }
                break;
            case "deposit FIAT":
                if(amount == 0) {
                    status = "amount can not be zero.";
                } else {
                     getFIATAcc();
                    beforeAcc = fiatAmount;
                    fiatAmount = fiatAmount + amount;
                    afterAcc = fiatAmount;
                    detail = "deposit";
                    type = "FIAT";
                    updateACC(type);
                    transLog(status, type, amount, beforeAcc, afterAcc);
                } 
                break;
            case "buy BITCOIN":
                getFIATAcc();
                beforeAcc = fiatAmount;
                x = amount*100000000;
                if(x <= fiatAmount) {
                    fiatAmount = fiatAmount - x;
                    afterAcc = fiatAmount;
                    detail = "withdraw";
                    type = "FIAT";
                    updateACC(type);
                    transLog(status, type, amount, beforeAcc, afterAcc);
                    getBITCOINAcc();
                    beforeAcc = bcAmount;
                    bcAmount = bcAmount + amount;
                    afterAcc = bcAmount;
                    detail = "deposit";
                    type = "BITCOIN";
                    updateACC(type);
                    transLog(status, type, amount, beforeAcc, afterAcc);
                } else {
                    status = "not enough fiat in your balance.";
                }
                break;
            case "sell BITCOIN":
                getBITCOINAcc();
                beforeAcc = bcAmount;
                if(bcAmount >= amount) {
                    x = amount*100000000;
                    bcAmount = bcAmount - amount;
                    afterAcc = bcAmount;
                    detail = "withdraw";
                    type = "BITCOIN";
                    updateACC(type);
                    transLog(status, type, amount, beforeAcc, afterAcc);
                    getFIATAcc();
                    beforeAcc = fiatAmount;
                    fiatAmount = fiatAmount + x;
                    afterAcc = fiatAmount;
                    detail = "deposit";
                    type = "FIAT";
                    updateACC(type);
                    transLog(status, type, amount, beforeAcc, afterAcc);
                } {
                    status = "can not over sell bitcoin on your balance.";
                }
                break;
            default:
                break;
        }
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
                fiatAmount = rs.getDouble("FIAT");
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
                    bcAmount = rs.getDouble("BITCOIN");
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
            query = String.format("UPDATE ACCOUNT SET FIAT = '%1$f' WHERE BBID = '%2$d'", fiatAmount, adminBBID);
        else
            query = String.format("UPDATE ACCOUNT SET BITCOIN = '%1$f' WHERE BBID = '%2$d'", bcAmount, adminBBID);
        try {
            DBConnect conn = new DBConnect();
            boolean result = conn.execute(query);
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

    private void transLog(String s, String type, double amount, double beforeAcc, double afterAcc) {
        String query ="";
        String status ="";
        query = String.format("INSERT INTO TRANS_LOG(ID, DETAIL, STATUS, TYPE, BEFORE_ACC, AMOUNT, AFTER_ACC)VALUES(null , '%1$s', '%2$s','%3$s', '%4$f', '%5$f', '%6$f')", detail, s, type, beforeAcc, amount, afterAcc);
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
    public DefaultTableModel showData(DefaultTableModel model) { 
        String query;
        try {
            DBConnect conn = new DBConnect();
            query = "SELECT * FROM TRANS_LOG"; 
            ResultSet rs = conn.getResult(query);
            while (rs.next()) {
                String id = Integer.toString(rs.getInt("ID"));
                String de = rs.getString("DETAIL");
                String st = rs.getString("STATUS");
                String ty = rs.getString("TYPE");
                String be = Double.toString(rs.getDouble("BEFORE_ACC"));
                String am = Double.toString(rs.getDouble("AMOUNT"));
                String af = Double.toString(rs.getDouble("AFTER_ACC"));
                String[] row = {id, de, st, ty, be, am, af};
                model.addRow(row);
                // critical variable name, beware of it!
            }
            conn.close();
        } catch (Exception ex) {
            //sss
        }
        return model;
    }
}
