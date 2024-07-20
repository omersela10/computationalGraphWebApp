package configs;


import graph.BinOpAgent;

public class MathExampleConfig implements Config {

    @Override
    public void create() {

        // Create BinOpAgent instances
        new BinOpAgent("plus", new String[]{"A", "B"}, new String[]{"R1"}, (x, y) -> x + y);
        new BinOpAgent("minus", new String[]{"A", "B"}, new String[]{"R2"}, (x, y) -> x - y);
        new BinOpAgent("mul", new String[]{"R1", "R2"}, new String[]{"R3"}, (x, y) -> x * y);
        //new BinOpAgent("div", new String[]{"R1", "R2"}, new String[]{"R3"}, (x, y) -> x / y);
        //new BinOpAgent("power", new String[]{"R1", "R2"}, new String[]{"R3"}, (x, y) -> Math.pow(x, y));

        // Create UnOpAgent instances
        //new UnOpAgent("inc", new String[]{"R1"}, new String[]{"R2"}, x -> x + 1);
        //new UnOpAgent("dec", new String[]{"R1"}, new String[]{"R2"}, x -> x - 1);
        //new UnOpAgent("squareroot", new String[]{"R1"}, new String[]{"R2"}, x -> Math.sqrt(x)); // Corrected spelling
        //new UnOpAgent("ln", new String[]{"R1"}, new String[]{"R2"}, x -> Math.log(x));
        //new UnOpAgent("log10", new String[]{"R1"}, new String[]{"R2"}, x -> Math.log10(x));
        //new UnOpAgent("exponent", new String[]{"R1"}, new String[]{"R2"}, x -> Math.exp(x));

    }

    @Override
    public String getName() {
        return "Math Example";
    }

    @Override
    public int getVersion() {
        return 1;
    }

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

}
