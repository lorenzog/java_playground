package localization.backend.localizers;

import java.util.Random;

import localization.backend.Main.PACKET_TYPE;
import localization.backend.utils.Position;

/**
 * this class is used to test the generation of a random Position; it receives
 * raw data but it does *NOT* use it.
 * 
 * @author lorenzo grespan
 * 
 */
public class TestLocalizer extends Localizer {

	int id;

	Random rnd;
	Double x, y, z;
	Double speed;
	long oldTime;
	
	long ownerList[];
	int nextOwner;

	public TestLocalizer() {
		rnd = new Random();
		x = rnd.nextDouble();
		y = rnd.nextDouble();
		z = rnd.nextDouble();

		/*
		 * we generate a random id here because we do not know the mapping blind
		 * id + GPS id -> real ID
		 */
		id = rnd.nextInt(42) + 1; /* so that id 0 is free */

		speed = rnd.nextDouble();
		oldTime = System.currentTimeMillis();
		
		initializeOwnerList();
		
		System.out.println("Test localizer ready to generate random positions");
	}
	
	private void initializeOwnerList() {
		ownerList = new long[5];
		ownerList[0] = 1;
		ownerList[1] = 2;
		ownerList[2] = 3;
		ownerList[3] = 4;
		ownerList[4] = 5;
		nextOwner = 0; 
	}

	@Override
	public void newData(byte[] inputLine, long timestamp) {
		Position p = new Position(timestamp);

		long currentTime = System.currentTimeMillis();
		p.setTimeStamp(currentTime);

		long dt = currentTime - oldTime;
		/* random drift */
		x = +speed * dt * rnd.nextGaussian() * 0.1;
		y = +speed * dt * rnd.nextGaussian() * 0.2;
		z = +speed * dt * rnd.nextGaussian() * 0.01;

		//let's stay inside phisycal limits..
		x = Math.sin(x)*180; // longitude
		y = Math.sin(y)*90; // latitude
		z = Math.sin(z); //??
		p.setType(PACKET_TYPE.VOID);
		// let's keep the current position between [-1,1]
		//p.setCoords(Math.sin(x), Math.sin(y), Math.sin(z));
		p.setCoords(new Double(x).longValue(), new Double(y).longValue(), new Double(z).longValue());
		// assign the current position to a person 
		p.setGlobalId(ownerList[nextOwner]);
		//update to the next owner
		if (++nextOwner >= ownerList.length){
			nextOwner = 0;
		}
		
		core.newComputedPosition(p);
	}

}
