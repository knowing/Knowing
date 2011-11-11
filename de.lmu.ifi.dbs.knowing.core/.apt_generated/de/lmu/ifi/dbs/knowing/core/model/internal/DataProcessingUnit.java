package de.lmu.ifi.dbs.knowing.core.model.internal;

import de.lmu.ifi.dbs.knowing.core.model.IConfiguration;
import de.lmu.ifi.dbs.knowing.core.model.IDataProcessingUnit;
import de.lmu.ifi.dbs.knowing.core.model.IEdge;
import de.lmu.ifi.dbs.knowing.core.model.INode;
import org.eclipse.sapphire.modeling.IModelParticle;
import org.eclipse.sapphire.modeling.ListBindingImpl;
import org.eclipse.sapphire.modeling.ModelElement;
import org.eclipse.sapphire.modeling.ModelElementHandle;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Resource;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueNormalizationService;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.util.MiscUtil;

@SuppressWarnings( "all" )

public final class DataProcessingUnit
    
    extends ModelElement
    implements IDataProcessingUnit
    
{
    private ModelElementHandle<IConfiguration> configuration;
    private Value<String> description;
    private ModelElementList<IEdge> edges;
    private Value<String> name;
    private ModelElementList<INode> nodes;
    private Value<String> tags;
    
    public DataProcessingUnit( final IModelParticle parent, final ModelProperty parentProperty, final Resource resource )
    {
        super( TYPE, parent, parentProperty, resource );
    }
    
    public DataProcessingUnit( final Resource resource )
    {
        super( TYPE, null, null, resource );
    }
    
    public IConfiguration getConfiguration()
    {
        synchronized( root() )
        {
            if( this.configuration == null )
            {
                refresh( PROP_CONFIGURATION, true );
            }
            
            return this.configuration.element();
        }
    }
    
    public Value<String> getDescription()
    {
        synchronized( root() )
        {
            if( this.description == null )
            {
                refresh( PROP_DESCRIPTION, true );
            }
            
            return this.description;
        }
    }
    
    public void setDescription( String value )
    {
        synchronized( root() )
        {
            if( value != null && value.equals( MiscUtil.EMPTY_STRING ) )
            {
                value = null;
            }
            
            value = PROP_DESCRIPTION.decodeKeywords( value );
            value = service( PROP_DESCRIPTION, ValueNormalizationService.class ).normalize( value );
            
            refresh( PROP_DESCRIPTION, true );
            
            if( ! equal( this.description.getText( false ), value ) )
            {
                resource().binding( PROP_DESCRIPTION ).write( value );
                refresh( PROP_DESCRIPTION, false );
            }
        }
    }
    
    public ModelElementList<IEdge> getEdges()
    {
        synchronized( root() )
        {
            if( this.edges == null )
            {
                refresh( PROP_EDGES, true );
            }
            
            return this.edges;
        }
    }
    
    public Value<String> getName()
    {
        synchronized( root() )
        {
            if( this.name == null )
            {
                refresh( PROP_NAME, true );
            }
            
            return this.name;
        }
    }
    
    public void setName( String value )
    {
        synchronized( root() )
        {
            if( value != null && value.equals( MiscUtil.EMPTY_STRING ) )
            {
                value = null;
            }
            
            value = PROP_NAME.decodeKeywords( value );
            value = service( PROP_NAME, ValueNormalizationService.class ).normalize( value );
            
            refresh( PROP_NAME, true );
            
            if( ! equal( this.name.getText( false ), value ) )
            {
                resource().binding( PROP_NAME ).write( value );
                refresh( PROP_NAME, false );
            }
        }
    }
    
    public ModelElementList<INode> getNodes()
    {
        synchronized( root() )
        {
            if( this.nodes == null )
            {
                refresh( PROP_NODES, true );
            }
            
            return this.nodes;
        }
    }
    
    public Value<String> getTags()
    {
        synchronized( root() )
        {
            if( this.tags == null )
            {
                refresh( PROP_TAGS, true );
            }
            
            return this.tags;
        }
    }
    
    public void setTags( String value )
    {
        synchronized( root() )
        {
            if( value != null && value.equals( MiscUtil.EMPTY_STRING ) )
            {
                value = null;
            }
            
            value = PROP_TAGS.decodeKeywords( value );
            value = service( PROP_TAGS, ValueNormalizationService.class ).normalize( value );
            
            refresh( PROP_TAGS, true );
            
            if( ! equal( this.tags.getText( false ), value ) )
            {
                resource().binding( PROP_TAGS ).write( value );
                refresh( PROP_TAGS, false );
            }
        }
    }
    
    public Status execute( final ProgressMonitor monitor )
    {
        synchronized( root() )
        {
            return IDataProcessingUnitOp.execute( this, monitor );
        }
    }
    
    protected void refreshProperty( ModelProperty property, final boolean force )
    {
        synchronized( root() )
        {
            property = property.refine( this );
            
            if( property == PROP_CONFIGURATION )
            {
                if( this.configuration == null )
                {
                    if( force == true )
                    {
                        this.configuration = new ModelElementHandle<IConfiguration>( this, PROP_CONFIGURATION );
                        this.configuration.init();
                        refreshPropertyEnablement( PROP_CONFIGURATION );
                    }
                }
                else
                {
                    final EnablementRefreshResult enablementRefreshResult = refreshPropertyEnablement( PROP_CONFIGURATION );
                    final boolean notified = this.configuration.refresh();
                    
                    if( ! notified && enablementRefreshResult.changed() )
                    {
                        notifyPropertyChangeListeners( PROP_CONFIGURATION, enablementRefreshResult );
                    }
                }
            }
            else if( property == PROP_DESCRIPTION )
            {
                if( this.description != null || force == true )
                {
                    final Value<String> oldValue = this.description;
                    
                    final String val = resource().binding( PROP_DESCRIPTION ).read();
                    
                    this.description = new Value<String>( this, PROP_DESCRIPTION, service( PROP_DESCRIPTION, ValueNormalizationService.class ).normalize( PROP_DESCRIPTION.encodeKeywords( val ) ) );
                    this.description.init();
                    
                    final EnablementRefreshResult enablementRefreshResult = refreshPropertyEnablement( PROP_DESCRIPTION );
                    
                    if( oldValue != null )
                    {
                        if( this.description.equals( oldValue ) )
                        {
                            this.description = oldValue;
                        }
                        
                        if( this.description != oldValue || enablementRefreshResult.changed() )
                        {
                            notifyPropertyChangeListeners( PROP_DESCRIPTION, enablementRefreshResult );
                        }
                    }
                }
            }
            else if( property == PROP_EDGES )
            {
                if( this.edges == null )
                {
                    if( force == true )
                    {
                        this.edges = new ModelElementList<IEdge>( this, PROP_EDGES );
                        final ListBindingImpl binding = resource().binding( PROP_EDGES );
                        this.edges.init( binding );
                        refreshPropertyEnablement( PROP_EDGES );
                    }
                }
                else
                {
                    final EnablementRefreshResult enablementRefreshResult = refreshPropertyEnablement( PROP_EDGES );
                    final boolean notified = this.edges.refresh();
                    
                    if( ! notified && enablementRefreshResult.changed() )
                    {
                        notifyPropertyChangeListeners( PROP_EDGES, enablementRefreshResult );
                    }
                }
            }
            else if( property == PROP_NAME )
            {
                if( this.name != null || force == true )
                {
                    final Value<String> oldValue = this.name;
                    
                    final String val = resource().binding( PROP_NAME ).read();
                    
                    this.name = new Value<String>( this, PROP_NAME, service( PROP_NAME, ValueNormalizationService.class ).normalize( PROP_NAME.encodeKeywords( val ) ) );
                    this.name.init();
                    
                    final EnablementRefreshResult enablementRefreshResult = refreshPropertyEnablement( PROP_NAME );
                    
                    if( oldValue != null )
                    {
                        if( this.name.equals( oldValue ) )
                        {
                            this.name = oldValue;
                        }
                        
                        if( this.name != oldValue || enablementRefreshResult.changed() )
                        {
                            notifyPropertyChangeListeners( PROP_NAME, enablementRefreshResult );
                        }
                    }
                }
            }
            else if( property == PROP_NODES )
            {
                if( this.nodes == null )
                {
                    if( force == true )
                    {
                        this.nodes = new ModelElementList<INode>( this, PROP_NODES );
                        final ListBindingImpl binding = resource().binding( PROP_NODES );
                        this.nodes.init( binding );
                        refreshPropertyEnablement( PROP_NODES );
                    }
                }
                else
                {
                    final EnablementRefreshResult enablementRefreshResult = refreshPropertyEnablement( PROP_NODES );
                    final boolean notified = this.nodes.refresh();
                    
                    if( ! notified && enablementRefreshResult.changed() )
                    {
                        notifyPropertyChangeListeners( PROP_NODES, enablementRefreshResult );
                    }
                }
            }
            else if( property == PROP_TAGS )
            {
                if( this.tags != null || force == true )
                {
                    final Value<String> oldValue = this.tags;
                    
                    final String val = resource().binding( PROP_TAGS ).read();
                    
                    this.tags = new Value<String>( this, PROP_TAGS, service( PROP_TAGS, ValueNormalizationService.class ).normalize( PROP_TAGS.encodeKeywords( val ) ) );
                    this.tags.init();
                    
                    final EnablementRefreshResult enablementRefreshResult = refreshPropertyEnablement( PROP_TAGS );
                    
                    if( oldValue != null )
                    {
                        if( this.tags.equals( oldValue ) )
                        {
                            this.tags = oldValue;
                        }
                        
                        if( this.tags != oldValue || enablementRefreshResult.changed() )
                        {
                            notifyPropertyChangeListeners( PROP_TAGS, enablementRefreshResult );
                        }
                    }
                }
            }
        }
    }
    
    public Object read( ModelProperty property )
    {
        property = property.refine( this );
        
        if( property == PROP_CONFIGURATION )
        {
            return getConfiguration();
        }
        else if( property == PROP_DESCRIPTION )
        {
            return getDescription();
        }
        else if( property == PROP_EDGES )
        {
            return getEdges();
        }
        else if( property == PROP_NAME )
        {
            return getName();
        }
        else if( property == PROP_NODES )
        {
            return getNodes();
        }
        else if( property == PROP_TAGS )
        {
            return getTags();
        }
        
        return super.read( property );
    }
    
    public void write( ValueProperty property, final Object value )
    {
        property = (ValueProperty) property.refine( this );
        
        if( property == PROP_DESCRIPTION )
        {
            setDescription( (String) value );
            return;
        }
        else if( property == PROP_NAME )
        {
            setName( (String) value );
            return;
        }
        else if( property == PROP_TAGS )
        {
            setTags( (String) value );
            return;
        }
        
        super.write( property, value );
    }
    
}
