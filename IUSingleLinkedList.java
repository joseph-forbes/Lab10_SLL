import java.util.*;

/**
 * Single-linked node implementation of IndexedUnsortedList.
 * An Iterator with working remove() method is implemented, but
 * ListIterator is unsupported.
 * 
 * @author 
 * 
 * @param <E> type to store
 */
public class IUSingleLinkedList<E> implements IndexedUnsortedList<E> {
	private LinearNode<E> front, rear;
	private int count;
	private int modCount;
	
	/** Creates an empty list */
	public IUSingleLinkedList() {
		front = rear = null;
		count = 0;
		modCount = 0;
	}

	@Override
	public void addToFront(E element) {
		LinearNode<E> newNode = new LinearNode<>(element);
		newNode.setNext(front);
		front = newNode;
		if(count == 0) rear = newNode; // Empty list

		count++;
		modCount++;
	}

	@Override
	public void addToRear(E element) {
		addElement(rear, element);
	}

	@Override
	public void add(E element) {
		addToRear(element);
	}

	@Override
	public void addAfter(E element, E target) {
		if(!contains(target)) throw new NoSuchElementException();
		LinearNode<E> current = front;
		while(current != null) {
			if(current.getElement().equals(target)) {
				addElement(current, element);
				return;
			}
			current = current.getNext();
		}
	}

	@Override
	public void add(int index, E element) {
		if(index < 0 || index > count) throw new IndexOutOfBoundsException();
		if(index == count) {
			// Adding to rear. 
			// Don't loop because for loop would leave the bounds of the list
			addToRear(element);
			return;
		}
		if(index == 0) {
			// Adding to front. 
			// Don't loop because loop only adds after the index given
			addToFront(element);
			return;
		}
		LinearNode<E> current = front;
		for(int i=0; i<index;i++) {
			current = current.getNext();
		}
		addElement(current, element);

		count++;
		modCount++;
	}

	@Override
	public E removeFirst() {
		if(isEmpty()) throw new NoSuchElementException();
		return remove(first());
	}

	@Override
	public E removeLast() {
		if(isEmpty()) throw new NoSuchElementException();

		return remove(last());
	}

	@Override
	public E remove(E element) {
		if (isEmpty()) {
			throw new NoSuchElementException();
		}
		LinearNode<E> current = front, previous = null;
		while (current != null && !current.getElement().equals(element)) {
			previous = current;
			current = current.getNext();
		}
		// Matching element not found
		if (current == null) {
			throw new NoSuchElementException();
		}
		return removeElement(previous, current);		
	}

	@Override
	public E remove(int index) {
		if(index < 0 || index >= count) throw new IndexOutOfBoundsException();
		LinearNode<E> current = front;
		for(int i=0; i<index;i++) {
			current = current.getNext();
		}
		return remove(current.getElement());
	}

	@Override
	public void set(int index, E element) {
		if(index < 0 || index >= count) throw new IndexOutOfBoundsException();
		LinearNode<E> current = front;
		for(int i=0; i<index;i++) {
			current = current.getNext();
		}
		current.setElement(element);
	}

	@Override
	public E get(int index) {
		if(index < 0 || index >= count) throw new IndexOutOfBoundsException();
		LinearNode<E> current = front;
		for(int i=0; i<index;i++) {
			current = current.getNext();
		}
		return current.getElement();
	}

	@Override
	public int indexOf(E element) {
		int index = 0;
		LinearNode<E> current = front;
		while(current != null) {
			if(current.getElement().equals(element)) return index;
			current = current.getNext();
			index++;
		}
		return -1;
	}

	@Override
	public E first() {
		if(isEmpty()) throw new NoSuchElementException(); 
		return front.getElement();
	}

	@Override
	public E last() {
		if(isEmpty()) throw new NoSuchElementException();
		return rear.getElement();
	}

	@Override
	public boolean contains(E target) {
		LinearNode<E> current = front;
		while(current != null) {
			if(current.getElement().equals(target)) return true;
			current = current.getNext();
		}
		return false;
	}

	@Override
	public boolean isEmpty() {
		return size() == 0;
	}

	@Override
	public int size() {
		return count;
	}

	@Override
	public String toString() {
		String result = "[";
		LinearNode<E> current = front;
		while(current != null) {
			result += current.getElement();
			current = current.getNext();
			if(current != null) result += ", ";
		}
		result += "]";
		return result;
	}

	private E removeElement(LinearNode<E> previous, LinearNode<E> current) {
		// Grab element
		E result = current.getElement();
		// If not the first element in the list
		if (previous != null) {
			previous.setNext(current.getNext());
		} else { // If the first element in the list
			front = current.getNext();
		}
		// If the last element in the list
		if (current.getNext() == null) {
			rear = previous;
		}
		count--;
		modCount++;

		return result;
	}
	private void addElement(LinearNode<E> current, E element) {
		if(current == null){ // Empty list
			LinearNode<E> newNode = new LinearNode<>(element);
			front = rear = newNode;
		} else {
			LinearNode<E> next = current.getNext();
			LinearNode<E> newNode = new LinearNode<>(element);
			current.setNext(newNode);
			newNode.setNext(next);
			if(current == rear) rear = newNode; // Adding to end of list
		}

		count++;
		modCount++;
	}
	@Override
	public Iterator<E> iterator() {
		return new SLLIterator();
	}

	/** Iterator for IUSingleLinkedList */
	private class SLLIterator implements Iterator<E> {
		private LinearNode<E> previous;
		private LinearNode<E> current;
		private LinearNode<E> next;
		private int iterModCount;
		
		/** Creates a new iterator for the list */
		public SLLIterator() {
			previous = null;
			current = null;
			next = front;
			iterModCount = modCount;
		}

		@Override
		public boolean hasNext() {
			return next != null;
		}

		@Override
		public E next() {
			if (iterModCount != modCount) {
				throw new ConcurrentModificationException();
			}

			if (next == null) {
				throw new NoSuchElementException();
			}

			// Move current to previous unless the last element was removed
			if (current != null) {
				previous = current;
			}

			current = next;
			next = next.getNext();

			return current.getElement();
		}
		
		@Override
		public void remove() {
			if (iterModCount != modCount) {
				throw new ConcurrentModificationException();
			}

			if (current == null) {
				throw new IllegalStateException();
			}

			removeElement(previous, current);

			// Allow the next call to next() to continue from the same position
			current = null;

			// Update the iterator's modification count
			iterModCount = modCount;
		}
	}

	// IGNORE THE FOLLOWING CODE
	// DON'T DELETE ME, HOWEVER!!!
	@Override
	public ListIterator<E> listIterator() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ListIterator<E> listIterator(int startingIndex) {
		throw new UnsupportedOperationException();
	}
}
