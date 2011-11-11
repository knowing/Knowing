package de.lmu.ifi.dbs.knowing.core.model.internal;

import de.lmu.ifi.dbs.knowing.core.model.INodeConstraint;
import org.eclipse.sapphire.modeling.IModelParticle;
import org.eclipse.sapphire.modeling.ModelElement;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.Resource;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueNormalizationService;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.serialization.ValueSerializationService;
import org.eclipse.sapphire.modeling.util.MiscUtil;

@SuppressWarnings( "all" )

public final class NodeConstraint
    
    extends ModelElement
    implements INodeConstraint
    
{
    private Value<Boolean> log;
    private Value<String> node;
    
    public NodeConstraint( final IModelParticle parent, final ModelProperty parentProperty, final Resource resource )
    {
        super( TYPE, parent, parentProperty, resource );
    }
    
    public NodeConstraint( final Resource resource )
    {
        super( TYPE, null, null, resource );
    }
    
    public Value<Boolean> getLog()
    {
        synchronized( root() )
        {
            if( this.log == null )
            {
                refresh( PROP_LOG, true );
            }
            
            return this.log;
        }
    }
    
    public void setLog( String value )
    {
        synchronized( root() )
        {
            if( value != null && value.equals( MiscUtil.EMPTY_STRING ) )
            {
                value = null;
            }
            
            value = PROP_LOG.decodeKeywords( value );
            value = service( PROP_LOG, ValueNormalizationService.class ).normalize( value );
            
            refresh( PROP_LOG, true );
            
            if( ! equal( this.log.getText( false ), value ) )
            {
                resource().binding( PROP_LOG ).write( value );
                refresh( PROP_LOG, false );
            }
        }
    }
    
    public void setLog( final Boolean value )
    {
        setLog( value != null ? service( PROP_LOG, ValueSerializationService.class ).encode( value ) : null );
    }
    
    public Value<String> getNode()
    {
        synchronized( root() )
        {
            if( this.node == null )
            {
                refresh( PROP_NODE, true );
            }
            
            return this.node;
        }
    }
    
    public void setNode( String value )
    {
        synchronized( root() )
        {
            if( value != null && value.equals( MiscUtil.EMPTY_STRING ) )
            {
                value = null;
            }
            
            value = PROP_NODE.decodeKeywords( value );
            value = service( PROP_NODE, ValueNormalizationService.class ).normalize( value );
            
            refresh( PROP_NODE, true );
            
            if( ! equal( this.node.getText( false ), value ) )
            {
                resource().binding( PROP_NODE ).write( value );
                refresh( PROP_NODE, false );
            }
        }
    }
    
    protected void refreshProperty( ModelProperty property, final boolean force )
    {
        synchronized( root() )
        {
            property = property.refine( this );
            
            if( property == PROP_LOG )
            {
                if( this.log != null || force == true )
                {
                    final Value<Boolean> oldValue = this.log;
                    
                    final String val = resource().binding( PROP_LOG ).read();
                    
                    this.log = new Value<Boolean>( this, PROP_LOG, service( PROP_LOG, ValueNormalizationService.class ).normalize( PROP_LOG.encodeKeywords( val ) ) );
                    this.log.init();
                    
                    final EnablementRefreshResult enablementRefreshResult = refreshPropertyEnablement( PROP_LOG );
                    
                    if( oldValue != null )
                    {
                        if( this.log.equals( oldValue ) )
                        {
                            this.log = oldValue;
                        }
                        
                        if( this.log != oldValue || enablementRefreshResult.changed() )
                        {
                            notifyPropertyChangeListeners( PROP_LOG, enablementRefreshResult );
                        }
                    }
                }
            }
            else if( property == PROP_NODE )
            {
                if( this.node != null || force == true )
                {
                    final Value<String> oldValue = this.node;
                    
                    final String val = resource().binding( PROP_NODE ).read();
                    
                    this.node = new Value<String>( this, PROP_NODE, service( PROP_NODE, ValueNormalizationService.class ).normalize( PROP_NODE.encodeKeywords( val ) ) );
                    this.node.init();
                    
                    final EnablementRefreshResult enablementRefreshResult = refreshPropertyEnablement( PROP_NODE );
                    
                    if( oldValue != null )
                    {
                        if( this.node.equals( oldValue ) )
                        {
                            this.node = oldValue;
                        }
                        
                        if( this.node != oldValue || enablementRefreshResult.changed() )
                        {
                            notifyPropertyChangeListeners( PROP_NODE, enablementRefreshResult );
                        }
                    }
                }
            }
        }
    }
    
    public Object read( ModelProperty property )
    {
        property = property.refine( this );
        
        if( property == PROP_LOG )
        {
            return getLog();
        }
        else if( property == PROP_NODE )
        {
            return getNode();
        }
        
        return super.read( property );
    }
    
    public void write( ValueProperty property, final Object value )
    {
        property = (ValueProperty) property.refine( this );
        
        if( property == PROP_LOG )
        {
            if( ! ( value instanceof String ) )
            {
                setLog( (Boolean) value );
            }
            else
            {
                setLog( (String) value );
            }
            
            return;
        }
        else if( property == PROP_NODE )
        {
            setNode( (String) value );
            return;
        }
        
        super.write( property, value );
    }
    
}
