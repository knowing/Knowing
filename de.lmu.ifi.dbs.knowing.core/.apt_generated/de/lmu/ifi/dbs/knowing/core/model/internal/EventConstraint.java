package de.lmu.ifi.dbs.knowing.core.model.internal;

import de.lmu.ifi.dbs.knowing.core.model.EventType;
import de.lmu.ifi.dbs.knowing.core.model.IEventConstraint;
import org.eclipse.sapphire.modeling.IModelParticle;
import org.eclipse.sapphire.modeling.ModelElement;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.Resource;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.util.MiscUtil;
import org.eclipse.sapphire.services.ValueNormalizationService;
import org.eclipse.sapphire.services.ValueSerializationMasterService;

@SuppressWarnings( "all" )

public final class EventConstraint
    
    extends ModelElement
    implements IEventConstraint
    
{
    private Value<Boolean> log;
    private Value<EventType> type;
    
    public EventConstraint( final IModelParticle parent, final ModelProperty parentProperty, final Resource resource )
    {
        super( TYPE, parent, parentProperty, resource );
    }
    
    public EventConstraint( final Resource resource )
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
        setLog( value != null ? service( PROP_LOG, ValueSerializationMasterService.class ).encode( value ) : null );
    }
    
    public Value<EventType> getType()
    {
        synchronized( root() )
        {
            if( this.type == null )
            {
                refresh( PROP_TYPE, true );
            }
            
            return this.type;
        }
    }
    
    public void setType( String value )
    {
        synchronized( root() )
        {
            if( value != null && value.equals( MiscUtil.EMPTY_STRING ) )
            {
                value = null;
            }
            
            value = PROP_TYPE.decodeKeywords( value );
            value = service( PROP_TYPE, ValueNormalizationService.class ).normalize( value );
            
            refresh( PROP_TYPE, true );
            
            if( ! equal( this.type.getText( false ), value ) )
            {
                resource().binding( PROP_TYPE ).write( value );
                refresh( PROP_TYPE, false );
            }
        }
    }
    
    public void setType( final EventType value )
    {
        setType( value != null ? service( PROP_TYPE, ValueSerializationMasterService.class ).encode( value ) : null );
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
            else if( property == PROP_TYPE )
            {
                if( this.type != null || force == true )
                {
                    final Value<EventType> oldValue = this.type;
                    
                    final String val = resource().binding( PROP_TYPE ).read();
                    
                    this.type = new Value<EventType>( this, PROP_TYPE, service( PROP_TYPE, ValueNormalizationService.class ).normalize( PROP_TYPE.encodeKeywords( val ) ) );
                    this.type.init();
                    
                    final EnablementRefreshResult enablementRefreshResult = refreshPropertyEnablement( PROP_TYPE );
                    
                    if( oldValue != null )
                    {
                        if( this.type.equals( oldValue ) )
                        {
                            this.type = oldValue;
                        }
                        
                        if( this.type != oldValue || enablementRefreshResult.changed() )
                        {
                            notifyPropertyChangeListeners( PROP_TYPE, enablementRefreshResult );
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
        else if( property == PROP_TYPE )
        {
            return getType();
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
        else if( property == PROP_TYPE )
        {
            if( ! ( value instanceof String ) )
            {
                setType( (EventType) value );
            }
            else
            {
                setType( (String) value );
            }
            
            return;
        }
        
        super.write( property, value );
    }
    
}
