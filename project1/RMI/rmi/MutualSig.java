package rmi;

import java.io.*;

public class MutualSig {
	public Integer stop;

	public MutualSig(Integer given_stop)
	{
		this.stop = 1;
		// 0 stands for running
		// 1 stands for stop
		// 2 stands for stop req sent
	}

}
