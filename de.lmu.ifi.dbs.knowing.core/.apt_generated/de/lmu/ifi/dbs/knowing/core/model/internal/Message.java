package de.lmu.ifi.dbs.knowing.core.model.internal;

import de.lmu.ifi.dbs.knowing.core.model.EventType;
import de.lmu.ifi.dbs.knowing.core.model.IMessage;
import org.eclipse.sapphire.modeling.IModelParticle;
import org.eclipse.sapphire.modeling.ModelElement;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.Resource;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueNormalizationService;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.serialization.ValueSerializationService;
import org.eclipse.sapphire.modeling.util.MiscUtil;
import weka.core.Instances;

@SuppressWarnings( "all" )

public final class Message
    
    extends ModelElement
    implements IMessage
    
{
    private Value<Instances> content;
    private Value<String> source;
    private Value<String> sourcePort;
    private Value<String> target;
    private Value<String> targetPort;
    private Value<EventType> type;
    
    public Message( final IModelParticle parent, final ModelProperty parentProperty, final Resource resource )
    {
        super( TYPE, parent, parentProperty, resource );
    }
    
    public Message( final Resource resource )
    {
        super( TYPE, null, null, resource );
    }
    
    public Value<Instances> getContent()
    {
        synchronized( root() )
        {
            if( this.content == null )
            {
                refresh( PROP_CONTENT, true );
            }
            
            return this.content;
        }
    }
    
    public void setContent( String value )
    {
        synchronized( root() )
        {
            if( value != null && value.equals( MiscUtil.EMPTY_STRING ) )
            {
                value = null;
            }
            
            value = PROP_CONTENT.decodeKeywords( value );
            value = service( PROP_CONTENT, ValueNormalizationService.class ).normalize( value );
            
            refresh( PROP_CONTENT, true );
            
            if( ! equal( this.content.getText( false ), value ) )
            {
                resource().binding( PROP_CONTENT ).write( value );
                refresh( PROP_CONTENT, false );
            }
        }
    }
    
    public void setContent( final Instances value )
    {
        setContent( value != null ? service( PROP_CONTENT, ValueSerializationService.class ).encode( value ) : null );
    }
    
    public Value<String> getSource()
    {
        synchronized( root() )
        {
            if( this.source == null )
            {
                refresh( PROP_SOURCE, true );
            }
            
            return this.source;
        }
    }
    
    public void setSource( String value )
    {
        synchronized( root() )
        {
            if( value != null && value.equals( MiscUtil.EMPTY_STRING ) )
            {
                value = null;
            }
            
            value = PROP_SOURCE.decodeKeywords( value );
            value = service( PROP_SOURCE, ValueNormalizationService.class ).normalize( value );
            
            refresh( PROP_SOURCE, true );
            
            if( ! equal( this.source.getText( false ), value ) )
            {
                resource().binding( PROP_SOURCE ).write( value );
                refresh( PROP_SOURCE, false );
            }
        }
    }
    
    public Value<String> getSourcePort()
    {
        synchronized( root() )
        {
            if( this.sourcePort == null )
            {
                refresh( PROP_SOURCE_PORT, true );
            }
            
            return this.sourcePort;
        }
    }
    
    public void setSourcePort( String value )
    {
        synchronized( root() )
        {
            if( value != null && value.equals( MiscUtil.EMPTY_STRING ) )
            {
                value = null;
            }
            
            value = PROP_SOURCE_PORT.decodeKeywords( value );
            value = service( PROP_SOURCE_PORT, ValueNormalizationService.class ).normalize( value );
            
            refresh( PROP_SOURCE_PORT, true );
            
            if( ! equal( this.sourcePort.getText( false ), value ) )
            {
                resource().binding( PROP_SOURCE_PORT ).write( value );
                refresh( PROP_SOURCE_PORT, false );
            }
        }
    }
    
    public Value<String> getTarget()
    {
        synchronized( root() )
        {
            if( this.target == null )
            {
                refresh( PROP_TARGET, true );
            }
            
            return this.target;
        }
    }
    
    public void setTarget( String value )
    {
        synchronized( root() )
        {
            if( value != null && value.equals( MiscUtil.EMPTY_STRING ) )
            {
                value = null;
            }
            
            value = PROP_TARGET.decodeKeywords( value );
            value = service( PROP_TARGET, ValueNormalizationService.class ).normalize( value );
            
            refresh( PROP_TARGET, true );
            
            if( ! equal( this.target.getText( false ), value ) )
            {
                resource().binding( PROP_TARGET ).write( value );
                refresh( PROP_TARGET, false );
            }
        }
    }
    
    public Value<String> getTargetPort()
    {
        synchronized( root() )
        {
            if( this.targetPort == null )
            {
                refresh( PROP_TARGET_PORT, true );
            }
            
            return this.targetPort;
        }
    }
    
    public void setTargetPort( String value )
    {
        synchronized( root() )
        {
            if( value != null && value.equals( MiscUtil.EMPTY_STRING ) )
            {
                value = null;
            }
            
            value = PROP_TARGET_PORT.decodeKeywords( value );
            value = service( PROP_TARGET_PORT, ValueNormalizationService.class ).normalize( value );
            
            refresh( PROP_TARGET_PORT, true );
            
            if( ! equal( this.targetPort.getText( false ), value ) )
            {
                resource().binding( PROP_TARGET_PORT ).write( value );
                refresh( PROP_TARGET_PORT, false );
            }
        }
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
        setType( value != null ? service( PROP_TYPE, ValueSerializationService.class ).encode( value ) : null );
    }
    
    protected void refreshProperty( ModelProperty property, final boolean force )
    {
        synchronized( root() )
        {
            property = property.refine( this );
            
            if( property == PROP_CONTENT )
            {
                if( this.content != null || force == true )
                {
                    final Value<Instances> oldValue = this.content;
                    
                    final String val = resource().binding( PROP_CONTENT ).read();
                    
                    this.content = new Value<Instances>( this, PROP_CONTENT, service( PROP_CONTENT, ValueNormalizationService.class ).normalize( PROP_CONTENT.encodeKeywords( val ) ) );
                    this.content.init();
                    
                    final EnablementRefreshResult enablementRefreshResult = refreshPropertyEnablement( PROP_CONTENT );
                    
                    if( oldValue != null )
                    {
                        if( this.content.equals( oldValue ) )
                        {
                            this.content = oldValue;
                        }
                        
                        if( this.content != oldValue || enablementRefreshResult.changed() )
                        {
                            notifyPropertyChangeListeners( PROP_CONTENT, enablementRefreshResult );
                        }
                    }
                }
            }
            else if( property == PROP_SOURCE )
            {
                if( this.source != null || force == true )
                {
                    final Value<String> oldValue = this.source;
                    
                    final String val = resource().binding( PROP_SOURCE ).read();
                    
                    this.source = new Value<String>( this, PROP_SOURCE, service( PROP_SOURCE, ValueNormalizationService.class ).normalize( PROP_SOURCE.encodeKeywords( val ) ) );
                    this.source.init();
                    
                    final EnablementRefreshResult enablementRefreshResult = refreshPropertyEnablement( PROP_SOURCE );
                    
                    if( oldValue != null )
                    {
                        if( this.source.equals( oldValue ) )
                        {
                            this.source = oldValue;
                        }
                        
                        if( this.source != oldValue || enablementRefreshResult.changed() )
                        {
                            notifyPropertyChangeListeners( PROP_SOURCE, enablementRefreshResult );
                        }
                    }
                }
            }
            else if( property == PROP_SOURCE_PORT )
            {
                if( this.sourcePort != null || force == true )
                {
                    final Value<String> oldValue = this.sourcePort;
                    
                    final String val = resource().binding( PROP_SOURCE_PORT ).read();
                    
                    this.sourcePort = new Value<String>( this, PROP_SOURCE_PORT, service( PROP_SOURCE_PORT, ValueNormalizationService.class ).normalize( PROP_SOURCE_PORT.encodeKeywords( val ) ) );
                    this.sourcePort.init();
                    
                    final EnablementRefreshResult enablementRefreshResult = refreshPropertyEnablement( PROP_SOURCE_PORT );
                    
                    if( oldValue != null )
                    {
                        if( this.sourcePort.equals( oldValue ) )
                        {
                            this.sourcePort = oldValue;
                        }
                        
                        if( this.sourcePort != oldValue || enablementRefreshResult.changed() )
                        {
                            notifyPropertyChangeListeners( PROP_SOURCE_PORT, enablementRefreshResult );
                        }
                    }
                }
            }
            else if( property == PROP_TARGET )
            {
                if( this.target != null || force == true )
                {
                    final Value<String> oldValue = this.target;
                    
                    final String val = resource().binding( PROP_TARGET ).read();
                    
                    this.target = new Value<String>( this, PROP_TARGET, service( PROP_TARGET, ValueNormalizationService.class ).normalize( PROP_TARGET.encodeKeywords( val ) ) );
                    this.target.init();
                    
                    final EnablementRefreshResult enablementRefreshResult = refreshPropertyEnablement( PROP_TARGET );
                    
                    if( oldValue != null )
                    {
                        if( this.target.equals( oldValue ) )
                        {
                            this.target = oldValue;
                        }
                        
                        if( this.target != oldValue || enablementRefreshResult.changed() )
                        {
                            notifyPropertyChangeListeners( PROP_TARGET, enablementRefreshResult );
                        }
                    }
                }
            }
            else if( property == PROP_TARGET_PORT )
            {
                if( this.targetPort != null || force == true )
                {
                    final Value<String> oldValue = this.targetPort;
                    
                    final String val = resource().binding( PROP_TARGET_PORT ).read();
                    
                    this.targetPort = new Value<String>( this, PROP_TARGET_PORT, service( PROP_TARGET_PORT, ValueNormalizationService.class ).normalize( PROP_TARGET_PORT.encodeKeywords( val ) ) );
                    this.targetPort.init();
                    
                    final EnablementRefreshResult enablementRefreshResult = refreshPropertyEnablement( PROP_TARGET_PORT );
                    
                    if( oldValue != null )
                    {
                        if( this.targetPort.equals( oldValue ) )
                        {
                            this.targetPort = oldValue;
                        }
                        
                        if( this.targetPort != oldValue || enablementRefreshResult.changed() )
                        {
                            notifyPropertyChangeListeners( PROP_TARGET_PORT, enablementRefreshResult );
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
        
        if( property == PROP_CONTENT )
        {
            return getContent();
        }
        else if( property == PROP_SOURCE )
        {
            return getSource();
        }
        else if( property == PROP_SOURCE_PORT )
        {
            return getSourcePort();
        }
        else if( property == PROP_TARGET )
        {
            return getTarget();
        }
        else if( property == PROP_TARGET_PORT )
        {
            return getTargetPort();
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
        
        if( property == PROP_CONTENT )
        {
            if( ! ( value instanceof String ) )
            {
                setContent( (Instances) value );
            }
            else
            {
                setContent( (String) value );
            }
            
            return;
        }
        else if( property == PROP_SOURCE )
        {
            setSource( (String) value );
            return;
        }
        else if( property == PROP_SOURCE_PORT )
        {
            setSourcePort( (String) value );
            return;
        }
        else if( property == PROP_TARGET )
        {
            setTarget( (String) value );
            return;
        }
        else if( property == PROP_TARGET_PORT )
        {
            setTargetPort( (String) value );
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
