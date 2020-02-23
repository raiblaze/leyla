/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.da.twilight.leyla;

import com.da.twilight.helper.TwilightApplication;
import com.da.twilight.helper.TwilightInstance;
import com.da.twilight.leyla.ui.UI;

/**
 *
 * @author ShadowWalker
 */
public class App implements TwilightInstance {
    public static void main(String[] args) {
        TwilightApplication.run( App.class, args);
    }

    @Override
    public void launch(String[] args) {
        UI.main(args);
    }
}
