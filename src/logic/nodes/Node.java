package logic.nodes;

import main.Main;

public abstract class Node {

    protected Main script;

    public Node(Main script){
        this.script = script;
    }
    public abstract boolean validate();
    public abstract void execute();
    public String status(){
        return this.getClass().getSimpleName();
    }
}
