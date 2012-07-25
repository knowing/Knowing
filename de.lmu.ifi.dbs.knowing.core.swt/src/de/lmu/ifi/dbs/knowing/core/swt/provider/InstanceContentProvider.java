/**
 * 
 */
package de.lmu.ifi.dbs.knowing.core.swt.provider;

import java.util.ArrayList;
import java.util.Collections;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import weka.core.Instance;
import weka.core.Instances;

/**
 * @author Nepomuk Seiler
 * @version 0.1
 * @since 23.03.2011
 */
public class InstanceContentProvider implements IStructuredContentProvider {

	private Instances content;
	private boolean append;

	/**
	 * append = false. Every setInput call overrides the old input
	 */
	public InstanceContentProvider() {
		this(false);
	}

	/**
	 * @param append
	 *            - should the instances be merged
	 */
	public InstanceContentProvider(boolean append) {
		this.append = append;
	}

	@Override
	public void dispose() {
		content = null;
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	@Override
	public Object[] getElements(Object inputElement) {
		if (!(inputElement instanceof Instances))
			return new Instances[0];
		Instances input = (Instances) inputElement;
		if (append)
			content = merge(content, input);
		else
			content = input;
		return (Instance[]) content.toArray(new Instance[content.size()]);
	}

	/**
	 * <p>This method merges two {@link Instances} horizontally where as <br>
	 * {@link Instances#mergeInstances(Instances, Instances)} merges two {@link Instances} vertically.</p>
	 * 
	 * <p>Test if the two datasets have equal headers via {@link Instances#equalHeaders(Instances)} and
	 * returns original on fail.</p>
	 * 
	 * <p>The {@link Instance}s in the additional dataset will be shallow copied</p>
	 * 
	 * @param original - the root dataset
	 * @param additional - the set which should be addded to original
	 * @return original if additional is null or headers are not equal
	 */
	@SuppressWarnings("unchecked")
	private Instances merge(Instances original, Instances additional) {
		if (original == null || additional == null)
			return (original == null) ? additional : original;
		if(!original.equalHeaders(additional))
			return original;
		
		ArrayList<Instance> instances = Collections.list(additional.enumerateInstances());
		for (Instance instance : instances) 
			original.add(instance);
		return original;
	}

}
