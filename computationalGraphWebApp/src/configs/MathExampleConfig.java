package configs;


import graph.BinOpAgent;

public class MathExampleConfig implements Config {

    @Override
    public void create() {

        // Create BinOpAgent instances
        new BinOpAgent("plus", new String[]{"A", "B"}, new String[]{"R1"}, (x, y) -> x + y);
        new BinOpAgent("minus", new String[]{"A", "B"}, new String[]{"R2"}, (x, y) -> x - y);
        new BinOpAgent("mul", new String[]{"R1", "R2"}, new String[]{"R3"}, (x, y) -> x * y);

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
