package model;

public class Line {
    /*Cell cell1;
    Cell cell2;*/
    //mine:
    Position pos1;
    Position pos2;

    /*public Line(Cell cell1, Cell cell2) {
        this.cell1 = cell1;
        this.cell2 = cell2;
    }*/
    //mine:
    public Line(Position pos1, Position pos2) {
        this.pos1 = pos1;
        this.pos2 = pos2;
    }

    public Position getPos1() {
        return pos1;
    }

    public Position getPos2() {
        return pos2;
    }
}
