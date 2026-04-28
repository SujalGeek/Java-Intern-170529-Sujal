enum CoordinateSystem{
    CARTESIAN,
    POLAR
}


class Point{
    private double x,y;

    protected Point(double x,double y)
    {
        this.x=x;
        this.y=y;
    }

    public Point(double a,double b, CoordinateSystem cs)
    {
        switch(cs)
        {
            case CARTESIAN:
                this.x=a;
                this.y=b;
            case POLAR:
                this.x = a*Math.cos(a);
                this.y= b*Math.sin(b);
                break;
            }

            // steps to add a new system
            // arugument CoordinateSystem
            // change ctor

            // singelton field
            public static final Point ORIGIN = new Point(0, 0)

            public static Point newCatersianPoint(double x,double y)
            {
                return new Point(x, y);
            }

            public static Point newPolarPoint(double rho,double thetha)
            {
                return new Point(rho*Math.cos(thetha), rho*Math.sin(thetha));
            }

            public static class Factory{
                public static Point newCartersianPoint(double x,double y)
                {
                    return new Point(x,y);
                }
            }

    }

}

class PointFactory
{
  public static Point newCartesianPoint(double x, double y)
  {
    return new Point(x,y);
  }
}

class Demo7{
    public static void main(String[] args) {
         Point point = new Point(2, 3, CoordinateSystem.CARTESIAN);
    Point origin = Point.ORIGIN;

    Point point1 = Point.Factory.newCartesianPoint(1, 2);

    }
}