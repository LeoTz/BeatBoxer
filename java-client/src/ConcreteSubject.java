import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Concrete implementation of the Subject interface using a thread pool
 * to notify observers asynchronously.
 */

// Concrete implementation of the Subject interface using a thread pool
// to notify observers asynchronously.
public class ConcreteSubject implements Subject{

	private ArrayList<Observer> observers;
	private ExecutorService threadPool;
	
    // Constructor initializes the observer list and thread pool.
	public ConcreteSubject(){
		observers = new ArrayList <Observer>();
		// Create a thread pool with a fixed number of threads
        threadPool = Executors.newFixedThreadPool(3);
	}
	
	// Notifies all registered observers asynchronously.
	public void notifyObservers() {
		for (Observer o : observers) {
			threadPool.execute(() -> o.update());  // fire off async
	    }
	}
	
	// Registers a new observer.
	public void registerObserver(Observer o) {
		observers.add(o);
	}
	
	// Removes a registered observer.
	public void removeObsever(Observer o) {
		observers.remove(o);
	}
	
	//shutdown method to clean up resources
    public void shutdown() {
        if (threadPool != null) {
            threadPool.shutdown();
        }
    }
}