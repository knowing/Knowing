package de.lmu.ifi.dbs.knowing.core.model;

import org.eclipse.sapphire.modeling.IExecutableModelElement;
import org.eclipse.sapphire.modeling.ImpliedElementProperty;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.annotations.DelegateImplementation;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlRootBinding;

import de.lmu.ifi.dbs.knowing.core.model.internal.IFileDataProcessingUnitOp;

@GenerateImpl
@XmlRootBinding( elementName = "Units" )
public interface IFileDataProcessingUnit extends IExecutableModelElement {

	
    ModelElementType TYPE = new ModelElementType( IFileDataProcessingUnit.class );
    
    // *** BugReport ***
    
    @Type( base = IDataProcessingUnit.class )
    @Label( standard = "dpu" )
    @XmlBinding( path = "dpu" )
    
    ImpliedElementProperty PROP_DATA_PROCESSING_UNIT = new ImpliedElementProperty( TYPE, "DataProcessingUnit" );
    
    IDataProcessingUnit getDataProcessingUnit();
    
    // *** Method: execute ***
    
    @DelegateImplementation( IFileDataProcessingUnitOp.class )
    Status execute( ProgressMonitor monitor );
    
}
