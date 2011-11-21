package de.lmu.ifi.dbs.knowing.core.model.internal;

import de.lmu.ifi.dbs.knowing.core.model.IProperty;
import org.eclipse.sapphire.modeling.IModelParticle;
import org.eclipse.sapphire.modeling.ModelElement;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.Resource;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueNormalizationService;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.util.MiscUtil;

@SuppressWarnings( "all" )

public final class Property
    
    extends ModelElement
    implements IProperty
    
{
    private Value<String> key;
    private Value<String> value;
    
    public Property( final IModelParticle parent, final ModelProperty parentProperty, final Resource resource )
    {
        super( TYPE, parent, parentProperty, resource );
    }
    
    public Property( final Resource resource )
    {
        super( TYPE, null, null, resource );
    }
    
    public Value<String> getKey()
    {
        synchronized( root() )
        {
            if( this.key == null )
            {
                refresh( PROP_KEY, true );
            }
            
            return this.key;
        }
    }
    
    public void setKey( String value )
    {
        synchronized( root() )
        {
            if( value != null && value.equals( MiscUtil.EMPTY_STRING ) )
            {
                value = null;
            }
            
            value = PROP_KEY.decodeKeywords( value );
            value = service( PROP_KEY, ValueNormalizationService.class ).normalize( value );
            
            refresh( PROP_KEY, true );
            
            if( ! equal( this.key.getText( false ), value ) )
            {
                resource().binding( PROP_KEY ).write( value );
                refresh( PROP_KEY, false );
            }
        }
    }
    
    public Value<String> getValue()
    {
        synchronized( root() )
        {
            if( this.value == null )
            {
                refresh( PROP_VALUE, true );
            }
            
            return this.value;
        }
    }
    
    public void setValue( String value )
    {
        synchronized( root() )
        {
            if( value != null && value.equals( MiscUtil.EMPTY_STRING ) )
            {
                value = null;
            }
            
            value = PROP_VALUE.decodeKeywords( value );
            value = service( PROP_VALUE, ValueNormalizationService.class ).normalize( value );
            
            refresh( PROP_VALUE, true );
            
            if( ! equal( this.value.getText( false ), value ) )
            {
                resource().binding( PROP_VALUE ).write( value );
                refresh( PROP_VALUE, false );
            }
        }
    }
    
    protected void refreshProperty( ModelProperty property, final boolean force )
    {
        synchronized( root() )
        {
            property = property.refine( this );
            
            if( property == PROP_KEY )
            {
                if( this.key != null || force == true )
                {
                    final Value<String> oldValue = this.key;
                    
                    final String val = resource().binding( PROP_KEY ).read();
                    
                    this.key = new Value<String>( this, PROP_KEY, service( PROP_KEY, ValueNormalizationService.class ).normalize( PROP_KEY.encodeKeywords( val ) ) );
                    this.key.init();
                    
                    final EnablementRefreshResult enablementRefreshResult = refreshPropertyEnablement( PROP_KEY );
                    
                    if( oldValue != null )
                    {
                        if( this.key.equals( oldValue ) )
                        {
                            this.key = oldValue;
                        }
                        
                        if( this.key != oldValue || enablementRefreshResult.changed() )
                        {
                            notifyPropertyChangeListeners( PROP_KEY, enablementRefreshResult );
                        }
                    }
                }
            }
            else if( property == PROP_VALUE )
            {
                if( this.value != null || force == true )
                {
                    final Value<String> oldValue = this.value;
                    
                    final String val = resource().binding( PROP_VALUE ).read();
                    
                    this.value = new Value<String>( this, PROP_VALUE, service( PROP_VALUE, ValueNormalizationService.class ).normalize( PROP_VALUE.encodeKeywords( val ) ) );
                    this.value.init();
                    
                    final EnablementRefreshResult enablementRefreshResult = refreshPropertyEnablement( PROP_VALUE );
                    
                    if( oldValue != null )
                    {
                        if( this.value.equals( oldValue ) )
                        {
                            this.value = oldValue;
                        }
                        
                        if( this.value != oldValue || enablementRefreshResult.changed() )
                        {
                            notifyPropertyChangeListeners( PROP_VALUE, enablementRefreshResult );
                        }
                    }
                }
            }
        }
    }
    
    public Object read( ModelProperty property )
    {
        property = property.refine( this );
        
        if( property == PROP_KEY )
        {
            return getKey();
        }
        else if( property == PROP_VALUE )
        {
            return getValue();
        }
        
        return super.read( property );
    }
    
    public void write( ValueProperty property, final Object value )
    {
        property = (ValueProperty) property.refine( this );
        
        if( property == PROP_KEY )
        {
            setKey( (String) value );
            return;
        }
        else if( property == PROP_VALUE )
        {
            setValue( (String) value );
            return;
        }
        
        super.write( property, value );
    }
    
}
