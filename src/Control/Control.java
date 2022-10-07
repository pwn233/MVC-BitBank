/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Control;

import Model.Model;
import javax.swing.JComboBox;

/**
 *
 * @author User
 */
public class Control {
    Model m = new Model();
    private double amount;
    private JComboBox JComboBox2;
    private String fiat;
    private String bitcoin;
    
    public void getAmount(double amount, JComboBox JComboBox2) {
        this.amount = amount;
        this.JComboBox2 = JComboBox2;
        makeTrans();
    }
    private void makeTrans() {
        m.makeTrans(amount, JComboBox2);
    }

    public String returnStatus(String type) {
        return m.updateACC(type);
    }

    public String getFiat() {
        fiat = m.getFiat();
        return fiat;
    }
    public String getBitcoin() {
        bitcoin = m.getBitcoin();
        return bitcoin;
    }
}
