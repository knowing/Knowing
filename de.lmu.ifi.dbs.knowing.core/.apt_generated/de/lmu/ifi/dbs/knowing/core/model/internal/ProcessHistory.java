package de.lmu.ifi.dbs.knowing.core.model.internal;

import de.lmu.ifi.dbs.knowing.core.model.IMessage;
import de.lmu.ifi.dbs.knowing.core.model.IProcessHistory;
import org.eclipse.sapphire.modeling.IModelParticle;
import org.eclipse.sapphire.modeling.ListBindingImpl;
import org.eclipse.sapphire.modeling.ModelElement;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.Resource;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueNormalizationService;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.util.MiscUtil;

@SuppressWarnings( "all" )

public final class ProcessHistory
    
    extends ModelElement
    implements IProcessHistory
    
{
    private ModelElementList<IMessage> messages;
    private Value<String> name;
    
    public ProcessHistory( final IModelParticle parent, final ModelProperty parentProperty, final Resource resource )
    {
        super( TYPE, parent, parentProperty, resource );
    }
    
    public ProcessHistory( final Resource resource )
    {
        super( TYPE, null, null, resource );
    }
    
    public ModelElementList<IMessage> getMessages()
    {
        synchronized( root() )
        {
            if( this.messages == null )
            {
                refresh( PROP_MESSAGES, true );
            }
            
            return this.messages;
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
    
    protected void refreshProperty( ModelProperty property, final boolean force )
    {
        synchronized( root() )
        {
            property = property.refine( this );
            
            if( property == PROP_MESSAGES )
            {
                if( this.messages == null )
                {
                    if( force == true )
                    {
                        this.messages = new ModelElementList<IMessage>( this, PROP_MESSAGES );
                        final ListBindingImpl binding = resource().binding( PROP_MESSAGES );
                        this.messages.init( binding );
                        refreshPropertyEnablement( PROP_MESSAGES );
                    }
                }
                else
                {
                    final EnablementRefreshResult enablementRefreshResult = refreshPropertyEnablement( PROP_MESSAGES );
                    final boolean notified = this.messages.refresh();
                    
                    if( ! notified && enablementRefreshResult.changed() )
                    {
                        notifyPropertyChangeListeners( PROP_MESSAGES, enablementRefreshResult );
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
        }
    }
    
    public Object read( ModelProperty property )
    {
        property = property.refine( this );
        
        if( property == PROP_MESSAGES )
        {
            return getMessages();
        }
        else if( property == PROP_NAME )
        {
            return getName();
        }
        
        return super.read( property );
    }
    
    public void write( ValueProperty property, final Object value )
    {
        property = (ValueProperty) property.refine( this );
        
        if( property == PROP_NAME )
        {
            setName( (String) value );
            return;
        }
        
        super.write( property, value );
    }
    
}
