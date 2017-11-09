package com.alive_homes.aliveapril;



/**
 * Created by alok on 14/3/16.
 */
public class Controller {
    int controllerPic;
    String controllerName;
    int value;
    String type;




    public Controller(String controllerName, int controllerPic, int value,String type) {
        this.controllerName = controllerName;
        this.controllerPic = controllerPic;
        this.value = value;
        this.type=type;
    }

    public int getControllerPic() {
        return controllerPic;
    }

    public String getControllerName() {
        return controllerName;
    }

    public String getType() {
        return type;
    }


    public int getvalue() {
        return value;
    }
}
