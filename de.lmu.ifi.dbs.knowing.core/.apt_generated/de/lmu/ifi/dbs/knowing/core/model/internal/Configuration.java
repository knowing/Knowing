package de.lmu.ifi.dbs.knowing.core.model.internal;

import de.lmu.ifi.dbs.knowing.core.model.IConfiguration;
import de.lmu.ifi.dbs.knowing.core.model.IEventConstraint;
import de.lmu.ifi.dbs.knowing.core.model.INodeConstraint;
import org.eclipse.sapphire.modeling.IModelParticle;
import org.eclipse.sapphire.modeling.ListBindingImpl;
import org.eclipse.sapphire.modeling.ModelElement;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.Resource;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.util.MiscUtil;
import org.eclipse.sapphire.services.ValueNormalizationService;
import org.eclipse.sapphire.services.ValueSerializationMasterService;

@SuppressWarnings( "all" )

public final class Configuration
    
    extends ModelElement
    implements IConfiguration
    
{
    private Value<Boolean> absolute;
    private ModelElementList<IEventConstraint> eventConstraints;
    private Value<Boolean> history;
    private ModelElementList<INodeConstraint> nodeConstraints;
    private Value<Path> output;
    
    public Configuration( final IModelParticle parent, final ModelProperty parentProperty, final Resource resource )
    {
        super( TYPE, parent, parentProperty, resource );
    }
    
    public Configuration( final Resource resource )
    {
        super( TYPE, null, null, resource );
    }
    
    public Value<Boolean> getAbsolute()
    {
        synchronized( root() )
        {
            if( this.absolute == null )
            {
                refresh( PROP_ABSOLUTE, true );
            }
            
            return this.absolute;
        }
    }
    
    public void setAbsolute( String value )
    {
        synchronized( root() )
        {
            if( value != null && value.equals( MiscUtil.EMPTY_STRING ) )
            {
                value = null;
            }
            
            value = PROP_ABSOLUTE.decodeKeywords( value );
            value = service( PROP_ABSOLUTE, ValueNormalizationService.class ).normalize( value );
            
            refresh( PROP_ABSOLUTE, true );
            
            if( ! equal( this.absolute.getText( false ), value ) )
            {
                resource().binding( PROP_ABSOLUTE ).write( value );
                refresh( PROP_ABSOLUTE, false );
            }
        }
    }
    
    public void setAbsolute( final Boolean value )
    {
        setAbsolute( value != null ? service( PROP_ABSOLUTE, ValueSerializationMasterService.class ).encode( value ) : null );
    }
    
    public ModelElementList<IEventConstraint> getEventConstraints()
    {
        synchronized( root() )
        {
            if( this.eventConstraints == null )
            {
                refresh( PROP_EVENT_CONSTRAINTS, true );
            }
            
            return this.eventConstraints;
        }
    }
    
    public Value<Boolean> getHistory()
    {
        synchronized( root() )
        {
            if( this.history == null )
            {
                refresh( PROP_HISTORY, true );
            }
            
            return this.history;
        }
    }
    
    public void setHistory( String value )
    {
        synchronized( root() )
        {
            if( value != null && value.equals( MiscUtil.EMPTY_STRING ) )
            {
                value = null;
            }
            
            value = PROP_HISTORY.decodeKeywords( value );
            value = service( PROP_HISTORY, ValueNormalizationService.class ).normalize( value );
            
            refresh( PROP_HISTORY, true );
            
            if( ! equal( this.history.getText( false ), value ) )
            {
                resource().binding( PROP_HISTORY ).write( value );
                refresh( PROP_HISTORY, false );
            }
        }
    }
    
    public void setHistory( final Boolean value )
    {
        setHistory( value != null ? service( PROP_HISTORY, ValueSerializationMasterService.class ).encode( value ) : null );
    }
    
    public ModelElementList<INodeConstraint> getNodeConstraints()
    {
        synchronized( root() )
        {
            if( this.nodeConstraints == null )
            {
                refresh( PROP_NODE_CONSTRAINTS, true );
            }
            
            return this.nodeConstraints;
        }
    }
    
    public Value<Path> getOutput()
    {
        synchronized( root() )
        {
            if( this.output == null )
            {
                refresh( PROP_OUTPUT, true );
            }
            
            return this.output;
        }
    }
    
    public void setOutput( String value )
    {
        synchronized( root() )
        {
            if( value != null && value.equals( MiscUtil.EMPTY_STRING ) )
            {
                value = null;
            }
            
            value = PROP_OUTPUT.decodeKeywords( value );
            value = service( PROP_OUTPUT, ValueNormalizationService.class ).normalize( value );
            
            refresh( PROP_OUTPUT, true );
            
            if( ! equal( this.output.getText( false ), value ) )
            {
                resource().binding( PROP_OUTPUT ).write( value );
                refresh( PROP_OUTPUT, false );
            }
        }
    }
    
    public void setOutput( final Path value )
    {
        setOutput( value != null ? service( PROP_OUTPUT, ValueSerializationMasterService.class ).encode( value ) : null );
    }
    
    protected void refreshProperty( ModelProperty property, final boolean force )
    {
        synchronized( root() )
        {
            property = property.refine( this );
            
            if( property == PROP_ABSOLUTE )
            {
                if( this.absolute != null || force == true )
                {
                    final Value<Boolean> oldValue = this.absolute;
                    
                    final String val = resource().binding( PROP_ABSOLUTE ).read();
                    
                    this.absolute = new Value<Boolean>( this, PROP_ABSOLUTE, service( PROP_ABSOLUTE, ValueNormalizationService.class ).normalize( PROP_ABSOLUTE.encodeKeywords( val ) ) );
                    this.absolute.init();
                    
                    final EnablementRefreshResult enablementRefreshResult = refreshPropertyEnablement( PROP_ABSOLUTE );
                    
                    if( oldValue != null )
                    {
                        if( this.absolute.equals( oldValue ) )
                        {
                            this.absolute = oldValue;
                        }
                        
                        if( this.absolute != oldValue || enablementRefreshResult.changed() )
                        {
                            notifyPropertyChangeListeners( PROP_ABSOLUTE, enablementRefreshResult );
                        }
                    }
                }
            }
            else if( property == PROP_EVENT_CONSTRAINTS )
            {
                if( this.eventConstraints == null )
                {
                    if( force == true )
                    {
                        this.eventConstraints = new ModelElementList<IEventConstraint>( this, PROP_EVENT_CONSTRAINTS );
                        final ListBindingImpl binding = resource().binding( PROP_EVENT_CONSTRAINTS );
                        this.eventConstraints.init( binding );
                        refreshPropertyEnablement( PROP_EVENT_CONSTRAINTS );
                    }
                }
                else
                {
                    final EnablementRefreshResult enablementRefreshResult = refreshPropertyEnablement( PROP_EVENT_CONSTRAINTS );
                    final boolean notified = this.eventConstraints.refresh();
                    
                    if( ! notified && enablementRefreshResult.changed() )
                    {
                        notifyPropertyChangeListeners( PROP_EVENT_CONSTRAINTS, enablementRefreshResult );
                    }
                }
            }
            else if( property == PROP_HISTORY )
            {
                if( this.history != null || force == true )
                {
                    final Value<Boolean> oldValue = this.history;
                    
                    final String val = resource().binding( PROP_HISTORY ).read();
                    
                    this.history = new Value<Boolean>( this, PROP_HISTORY, service( PROP_HISTORY, ValueNormalizationService.class ).normalize( PROP_HISTORY.encodeKeywords( val ) ) );
                    this.history.init();
                    
                    final EnablementRefreshResult enablementRefreshResult = refreshPropertyEnablement( PROP_HISTORY );
                    
                    if( oldValue != null )
                    {
                        if( this.history.equals( oldValue ) )
                        {
                            this.history = oldValue;
                        }
                        
                        if( this.history != oldValue || enablementRefreshResult.changed() )
                        {
                            notifyPropertyChangeListeners( PROP_HISTORY, enablementRefreshResult );
                        }
                    }
                }
            }
            else if( property == PROP_NODE_CONSTRAINTS )
            {
                if( this.nodeConstraints == null )
                {
                    if( force == true )
                    {
                        this.nodeConstraints = new ModelElementList<INodeConstraint>( this, PROP_NODE_CONSTRAINTS );
                        final ListBindingImpl binding = resource().binding( PROP_NODE_CONSTRAINTS );
                        this.nodeConstraints.init( binding );
                        refreshPropertyEnablement( PROP_NODE_CONSTRAINTS );
                    }
                }
                else
                {
                    final EnablementRefreshResult enablementRefreshResult = refreshPropertyEnablement( PROP_NODE_CONSTRAINTS );
                    final boolean notified = this.nodeConstraints.refresh();
                    
                    if( ! notified && enablementRefreshResult.changed() )
                    {
                        notifyPropertyChangeListeners( PROP_NODE_CONSTRAINTS, enablementRefreshResult );
                    }
                }
            }
            else if( property == PROP_OUTPUT )
            {
                if( this.output != null || force == true )
                {
                    final Value<Path> oldValue = this.output;
                    
                    final String val = resource().binding( PROP_OUTPUT ).read();
                    
                    this.output = new Value<Path>( this, PROP_OUTPUT, service( PROP_OUTPUT, ValueNormalizationService.class ).normalize( PROP_OUTPUT.encodeKeywords( val ) ) );
                    this.output.init();
                    
                    final EnablementRefreshResult enablementRefreshResult = refreshPropertyEnablement( PROP_OUTPUT );
                    
                    if( oldValue != null )
                    {
                        if( this.output.equals( oldValue ) )
                        {
                            this.output = oldValue;
                        }
                        
                        if( this.output != oldValue || enablementRefreshResult.changed() )
                        {
                            notifyPropertyChangeListeners( PROP_OUTPUT, enablementRefreshResult );
                        }
                    }
                }
            }
        }
    }
    
    public Object read( ModelProperty property )
    {
        property = property.refine( this );
        
        if( property == PROP_ABSOLUTE )
        {
            return getAbsolute();
        }
        else if( property == PROP_EVENT_CONSTRAINTS )
        {
            return getEventConstraints();
        }
        else if( property == PROP_HISTORY )
        {
            return getHistory();
        }
        else if( property == PROP_NODE_CONSTRAINTS )
        {
            return getNodeConstraints();
        }
        else if( property == PROP_OUTPUT )
        {
            return getOutput();
        }
        
        return super.read( property );
    }
    
    public void write( ValueProperty property, final Object value )
    {
        property = (ValueProperty) property.refine( this );
        
        if( property == PROP_ABSOLUTE )
        {
            if( ! ( value instanceof String ) )
            {
                setAbsolute( (Boolean) value );
            }
            else
            {
                setAbsolute( (String) value );
            }
            
            return;
        }
        else if( property == PROP_HISTORY )
        {
            if( ! ( value instanceof String ) )
            {
                setHistory( (Boolean) value );
            }
            else
            {
                setHistory( (String) value );
            }
            
            return;
        }
        else if( property == PROP_OUTPUT )
        {
            if( ! ( value instanceof String ) )
            {
                setOutput( (Path) value );
            }
            else
            {
                setOutput( (String) value );
            }
            
            return;
        }
        
        super.write( property, value );
    }
    
}
