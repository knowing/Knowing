package de.lmu.ifi.dbs.knowing.core.model.internal;

import de.lmu.ifi.dbs.knowing.core.model.INode;
import de.lmu.ifi.dbs.knowing.core.model.IProperty;
import de.lmu.ifi.dbs.knowing.core.model.NodeType;
import org.eclipse.sapphire.java.JavaType;
import org.eclipse.sapphire.java.JavaTypeName;
import org.eclipse.sapphire.modeling.IModelParticle;
import org.eclipse.sapphire.modeling.ListBindingImpl;
import org.eclipse.sapphire.modeling.ModelElement;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.ReferenceValue;
import org.eclipse.sapphire.modeling.Resource;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueNormalizationService;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.serialization.ValueSerializationService;
import org.eclipse.sapphire.modeling.util.MiscUtil;

@SuppressWarnings( "all" )

public final class Node
    
    extends ModelElement
    implements INode
    
{
    private ReferenceValue<JavaTypeName,JavaType> factoryId;
    private Value<String> id;
    private ModelElementList<IProperty> properties;
    private Value<NodeType> type;
    
    public Node( final IModelParticle parent, final ModelProperty parentProperty, final Resource resource )
    {
        super( TYPE, parent, parentProperty, resource );
    }
    
    public Node( final Resource resource )
    {
        super( TYPE, null, null, resource );
    }
    
    public ReferenceValue<JavaTypeName,JavaType> getFactoryId()
    {
        synchronized( root() )
        {
            if( this.factoryId == null )
            {
                refresh( PROP_FACTORY_ID, true );
            }
            
            return this.factoryId;
        }
    }
    
    public void setFactoryId( String value )
    {
        synchronized( root() )
        {
            if( value != null && value.equals( MiscUtil.EMPTY_STRING ) )
            {
                value = null;
            }
            
            value = PROP_FACTORY_ID.decodeKeywords( value );
            value = service( PROP_FACTORY_ID, ValueNormalizationService.class ).normalize( value );
            
            refresh( PROP_FACTORY_ID, true );
            
            if( ! equal( this.factoryId.getText( false ), value ) )
            {
                resource().binding( PROP_FACTORY_ID ).write( value );
                refresh( PROP_FACTORY_ID, false );
            }
        }
    }
    
    public void setFactoryId( final JavaTypeName value )
    {
        setFactoryId( value != null ? service( PROP_FACTORY_ID, ValueSerializationService.class ).encode( value ) : null );
    }
    
    public Value<String> getId()
    {
        synchronized( root() )
        {
            if( this.id == null )
            {
                refresh( PROP_ID, true );
            }
            
            return this.id;
        }
    }
    
    public void setId( String value )
    {
        synchronized( root() )
        {
            if( value != null && value.equals( MiscUtil.EMPTY_STRING ) )
            {
                value = null;
            }
            
            value = PROP_ID.decodeKeywords( value );
            value = service( PROP_ID, ValueNormalizationService.class ).normalize( value );
            
            refresh( PROP_ID, true );
            
            if( ! equal( this.id.getText( false ), value ) )
            {
                resource().binding( PROP_ID ).write( value );
                refresh( PROP_ID, false );
            }
        }
    }
    
    public ModelElementList<IProperty> getProperties()
    {
        synchronized( root() )
        {
            if( this.properties == null )
            {
                refresh( PROP_PROPERTIES, true );
            }
            
            return this.properties;
        }
    }
    
    public Value<NodeType> getType()
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
    
    public void setType( final NodeType value )
    {
        setType( value != null ? service( PROP_TYPE, ValueSerializationService.class ).encode( value ) : null );
    }
    
    protected void refreshProperty( ModelProperty property, final boolean force )
    {
        synchronized( root() )
        {
            property = property.refine( this );
            
            if( property == PROP_FACTORY_ID )
            {
                if( this.factoryId != null || force == true )
                {
                    final ReferenceValue<JavaTypeName,JavaType> oldValue = this.factoryId;
                    
                    final String val = resource().binding( PROP_FACTORY_ID ).read();
                    
                    this.factoryId = new ReferenceValue<JavaTypeName,JavaType>( this, PROP_FACTORY_ID, service( PROP_FACTORY_ID, ValueNormalizationService.class ).normalize( PROP_FACTORY_ID.encodeKeywords( val ) ) );
                    this.factoryId.init();
                    
                    final EnablementRefreshResult enablementRefreshResult = refreshPropertyEnablement( PROP_FACTORY_ID );
                    
                    if( oldValue != null )
                    {
                        if( this.factoryId.equals( oldValue ) )
                        {
                            this.factoryId = oldValue;
                        }
                        
                        if( this.factoryId != oldValue || enablementRefreshResult.changed() )
                        {
                            notifyPropertyChangeListeners( PROP_FACTORY_ID, enablementRefreshResult );
                        }
                    }
                }
            }
            else if( property == PROP_ID )
            {
                if( this.id != null || force == true )
                {
                    final Value<String> oldValue = this.id;
                    
                    final String val = resource().binding( PROP_ID ).read();
                    
                    this.id = new Value<String>( this, PROP_ID, service( PROP_ID, ValueNormalizationService.class ).normalize( PROP_ID.encodeKeywords( val ) ) );
                    this.id.init();
                    
                    final EnablementRefreshResult enablementRefreshResult = refreshPropertyEnablement( PROP_ID );
                    
                    if( oldValue != null )
                    {
                        if( this.id.equals( oldValue ) )
                        {
                            this.id = oldValue;
                        }
                        
                        if( this.id != oldValue || enablementRefreshResult.changed() )
                        {
                            notifyPropertyChangeListeners( PROP_ID, enablementRefreshResult );
                        }
                    }
                }
            }
            else if( property == PROP_PROPERTIES )
            {
                if( this.properties == null )
                {
                    if( force == true )
                    {
                        this.properties = new ModelElementList<IProperty>( this, PROP_PROPERTIES );
                        final ListBindingImpl binding = resource().binding( PROP_PROPERTIES );
                        this.properties.init( binding );
                        refreshPropertyEnablement( PROP_PROPERTIES );
                    }
                }
                else
                {
                    final EnablementRefreshResult enablementRefreshResult = refreshPropertyEnablement( PROP_PROPERTIES );
                    final boolean notified = this.properties.refresh();
                    
                    if( ! notified && enablementRefreshResult.changed() )
                    {
                        notifyPropertyChangeListeners( PROP_PROPERTIES, enablementRefreshResult );
                    }
                }
            }
            else if( property == PROP_TYPE )
            {
                if( this.type != null || force == true )
                {
                    final Value<NodeType> oldValue = this.type;
                    
                    final String val = resource().binding( PROP_TYPE ).read();
                    
                    this.type = new Value<NodeType>( this, PROP_TYPE, service( PROP_TYPE, ValueNormalizationService.class ).normalize( PROP_TYPE.encodeKeywords( val ) ) );
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
        
        if( property == PROP_FACTORY_ID )
        {
            return getFactoryId();
        }
        else if( property == PROP_ID )
        {
            return getId();
        }
        else if( property == PROP_PROPERTIES )
        {
            return getProperties();
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
        
        if( property == PROP_FACTORY_ID )
        {
            if( ! ( value instanceof String ) )
            {
                setFactoryId( (JavaTypeName) value );
            }
            else
            {
                setFactoryId( (String) value );
            }
            
            return;
        }
        else if( property == PROP_ID )
        {
            setId( (String) value );
            return;
        }
        else if( property == PROP_TYPE )
        {
            if( ! ( value instanceof String ) )
            {
                setType( (NodeType) value );
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
