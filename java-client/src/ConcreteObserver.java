public class ConcreteObserver implements Observer {
	private Subject subject;

	// Constructs a ConcreteObserver and registers it with the given subject.
	public ConcreteObserver(Subject subject)
		{
			this.subject = subject;
			this.subject.registerObserver(this);
		}
	// Called when the subject notifies observers.
	// Needs to be overriden 
	public void update() {
		System.out.println("do nothing from "+this);
	}
}