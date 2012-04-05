package de.yogularm.components;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

import de.yogularm.drawing.AnimatedImage;
import de.yogularm.drawing.Animation;
import de.yogularm.drawing.Drawable;
import de.yogularm.event.Event;
import de.yogularm.geometry.Vector;
import de.yogularm.network.InvalidPacketException;

public class Component implements Locatable {
	private int id;
	private Vector position;
	private transient ComponentCollection collection;
	private transient boolean isRemoved = false;
	private Drawable drawable;
	private boolean isNetworkComponent;
	private boolean hasChanged;
	
	private static int nextID = new Random().nextInt(0x10000);

	/**
	 * An event that is called when this component is moved
	 * 
	 * The event parameter specifies the former position.
	 */
	public final Event<Vector> onMoved = new Event<Vector>(this);

	public Component(ComponentCollection collection) {
		if (collection == null)
			throw new NullPointerException("collection is null");
		this.collection = collection;
		position = Vector.getZero();
		id = nextID;
		nextID++;
	}
	
	public int getID() {
		return id;
	}

	public ComponentCollection getCollection() {
		return collection;
	}

	public Vector getPosition() {
		return position;
	}

	public void setPosition(Vector position) {
		if (position == null)
			throw new IllegalArgumentException("position is null");

		Vector oldPosition = this.position;
		this.position = position;
		if (!position.equals(oldPosition))
			onMoved.call(oldPosition);
	}

	public void update(float elapsedTime) {
		if (drawable != null)
			drawable.update(elapsedTime);
	}

	public void remove() {
		isRemoved = true;
	}

	public boolean isToRemove() {
		return isRemoved;
	}

	protected void setDrawable(Drawable drawable) {
		this.drawable = drawable;
	}

	protected void setAnimation(Animation animation) {
		if ((this.drawable instanceof AnimatedImage)) {
			AnimatedImage animatedImage = (AnimatedImage) drawable;
			if (animatedImage.getAnimation() == animation)
				return;
		}
		drawable = animation.getInstance();
	}

	public Drawable getDrawable() {
		return drawable;
	}
	
	/**
	 * Writes this object's state to a stream
	 * 
	 * A newly-created object of the same class as this object can read the data and result in the
	 * same object as this. Note that this method does not save this object's class name.
	 * 
	 * @param stream The stream to write into
	 * @throws IOException Some i/o error occurred
	 */
	public final void write(OutputStream stream) throws IOException {
		write(new DataOutputStream(stream), 0);
	}
	
	/**
	 * The actual writing routine. Should be overridden by all subclasses which have to add their own
	 * data.
	 * 
	 * An override should look like this:
	 * 
	 * <example>
	 * protected void write(DataOutputStream stream, length) throws IOException {
	 *   super(length + 4);
	 *   stream.writeInt(theInteger);Expected
	 * }
	 * </example>
	 * 
	 * Where 4 is the number of bytes written by the code in the particular override. Overriding the
	 * method this way, the length sums up to the total length and Component.write() knows the length
	 * before a single single byte is written - so it can prepend the length before the actual data
	 * comes.
	 * 
	 * @param stream The stream to write into
	 * @param length The count of bytes used plus the count of bytes used by subclasses
	 */
	protected void write(DataOutputStream stream, int length) throws IOException {
		length += 3 * 4;
		stream.writeInt(length);
		
		stream.writeInt(id);
		stream.writeFloat(position.getX());
		stream.writeFloat(position.getY());
	}
	
	/**
	 * Loads this object's state from the given stream, assuming this component has just been created.
	 * 
	 * The component must not be changed since being constructed; otherwise, this method may have
	 * unexpected results.
	 * 
	 * @param stream The stream to read from
	 * @throws IOException Some i/o error occured
	 * @throws InvalidPacketException The packet in the stream was invalid, but could be skipped
	 */
	public final void read(InputStream stream) throws IOException {
		read(new DataInputStream(stream), 0);
	}

	/**
	 * The actual reading routine. Should be overridden by all subclasses which add their own data
	 * 
	 * An override should look like this:
	 * 
	 * <example>
	 * protected void read(DataInputStream stream, length) throws IOException {
	 *   super(length + 4);
	 *   theInteger = stream.readInt();
	 * }
	 * </example>
	 * 
	 * Where 4 is the number of bytes that will be read by this particular override. Overriding the
	 * method this way, the length sums up to the total length and Component.read() knows the length
	 * and can compare it to the actual count of bytes received (which is received before the actual
	 * data). If the lengths do not equal, it is possible to skip the whole packet.
	 * 
	 * @param stream The stream to read from
	 * @param length The count of bytes that will be read plus the count of bytes read by subclasses
	 * @throws InvalidPacketException The packet in the stream was invalid, but could be skipped
	 */
	protected void read(DataInputStream stream, int length) throws IOException {
		length += 3 * 4;
		int actualLength = stream.readInt();
		if (actualLength != length) {
			stream.skip(actualLength);
			throw new InvalidPacketException(String.format("Expected to read %d bytes but received %d", length, actualLength));
		}
		
		id = stream.readInt();
		position = new Vector(stream.readFloat(), stream.readFloat());
	}

	public boolean isNetworkComponent() {
		return isNetworkComponent;
	}

	public void setNetworkComponent(boolean isNetworkComponent) {
		this.isNetworkComponent = isNetworkComponent;
	}
	
	public boolean hasChanged() {
		return hasChanged;
	}
	
	public void setChanged() {
		hasChanged = true;
	}
	
	public void clearChanged() {
		hasChanged = false;
	}
}
