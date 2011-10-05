package de.lmu.ifi.dbs.knowing.core.model.internal;

import org.eclipse.sapphire.modeling.DelimitedListBindingImpl;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.xml.XmlElement;
import org.eclipse.sapphire.modeling.xml.XmlNamespaceResolver;
import org.eclipse.sapphire.modeling.xml.XmlNode;
import org.eclipse.sapphire.modeling.xml.XmlPath;
import org.eclipse.sapphire.modeling.xml.XmlResource;

public class InstancesListController extends DelimitedListBindingImpl {

	private static final XmlNamespaceResolver NAMESPACE_RESOLVER = new InstancesListController.InstancesNamespacesResolver();
	private static final XmlPath PATH_INSTANCE = new XmlPath("instance", NAMESPACE_RESOLVER);

	@Override
	protected String readListString() {
		final IModelElement parent = element();
		final XmlElement parentXmlElement = ((XmlResource) parent.resource()).getXmlElement();

		if (parentXmlElement == null) {
			return null;
		}

		final XmlNode listXmlNode = parentXmlElement.getChildNode(PATH_INSTANCE, false);

		if (listXmlNode == null) {
			return null;
		}

		return listXmlNode.getText();
	}

	@Override
	protected void writeListString(String str) {
		System.out.println("WriteListString: " + str);
	}

	public static class InstancesNamespacesResolver extends XmlNamespaceResolver {

		@Override
		public String resolve(String prefix) {
			System.out.println("Prefix: " + prefix);
			return null;
		}

	}

}
