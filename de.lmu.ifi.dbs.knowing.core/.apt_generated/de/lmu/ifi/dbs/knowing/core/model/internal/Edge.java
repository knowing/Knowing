package de.lmu.ifi.dbs.knowing.core.model.internal;

import de.lmu.ifi.dbs.knowing.core.model.IEdge;
import de.lmu.ifi.dbs.knowing.core.model.INode;
import org.eclipse.sapphire.modeling.IModelParticle;
import org.eclipse.sapphire.modeling.ModelElement;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.ReferenceValue;
import org.eclipse.sapphire.modeling.Resource;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.util.MiscUtil;
import org.eclipse.sapphire.services.ValueNormalizationService;

@SuppressWarnings( "all" )

public final class Edge
    
    extends ModelElement
    implements IEdge
    
{
    private Value<String> id;
    private ReferenceValue<String,INode> source;
    private Value<String> sourcePort;
    private ReferenceValue<String,INode> target;
    private Value<String> targetPort;
    private Value<String> weight;
    
    public Edge( final IModelParticle parent, final ModelProperty parentProperty, final Resource resource )
    {
        super( TYPE, parent, parentProperty, resource );
    }
    
    public Edge( final Resource resource )
    {
        super( TYPE, null, null, resource );
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
    
    public ReferenceValue<String,INode> getSource()
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
    
    public ReferenceValue<String,INode> getTarget()
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
    
    public Value<String> getWeight()
    {
        synchronized( root() )
        {
            if( this.weight == null )
            {
                refresh( PROP_WEIGHT, true );
            }
            
            return this.weight;
        }
    }
    
    public void setWeight( String value )
    {
        synchronized( root() )
        {
            if( value != null && value.equals( MiscUtil.EMPTY_STRING ) )
            {
                value = null;
            }
            
            value = PROP_WEIGHT.decodeKeywords( value );
            value = service( PROP_WEIGHT, ValueNormalizationService.class ).normalize( value );
            
            refresh( PROP_WEIGHT, true );
            
            if( ! equal( this.weight.getText( false ), value ) )
            {
                resource().binding( PROP_WEIGHT ).write( value );
                refresh( PROP_WEIGHT, false );
            }
        }
    }
    
    protected void refreshProperty( ModelProperty property, final boolean force )
    {
        synchronized( root() )
        {
            property = property.refine( this );
            
            if( property == PROP_ID )
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
            else if( property == PROP_SOURCE )
            {
                if( this.source != null || force == true )
                {
                    final ReferenceValue<String,INode> oldValue = this.source;
                    
                    final String val = resource().binding( PROP_SOURCE ).read();
                    
                    this.source = new ReferenceValue<String,INode>( this, PROP_SOURCE, service( PROP_SOURCE, ValueNormalizationService.class ).normalize( PROP_SOURCE.encodeKeywords( val ) ) );
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
                    final ReferenceValue<String,INode> oldValue = this.target;
                    
                    final String val = resource().binding( PROP_TARGET ).read();
                    
                    this.target = new ReferenceValue<String,INode>( this, PROP_TARGET, service( PROP_TARGET, ValueNormalizationService.class ).normalize( PROP_TARGET.encodeKeywords( val ) ) );
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
            else if( property == PROP_WEIGHT )
            {
                if( this.weight != null || force == true )
                {
                    final Value<String> oldValue = this.weight;
                    
                    final String val = resource().binding( PROP_WEIGHT ).read();
                    
                    this.weight = new Value<String>( this, PROP_WEIGHT, service( PROP_WEIGHT, ValueNormalizationService.class ).normalize( PROP_WEIGHT.encodeKeywords( val ) ) );
                    this.weight.init();
                    
                    final EnablementRefreshResult enablementRefreshResult = refreshPropertyEnablement( PROP_WEIGHT );
                    
                    if( oldValue != null )
                    {
                        if( this.weight.equals( oldValue ) )
                        {
                            this.weight = oldValue;
                        }
                        
                        if( this.weight != oldValue || enablementRefreshResult.changed() )
                        {
                            notifyPropertyChangeListeners( PROP_WEIGHT, enablementRefreshResult );
                        }
                    }
                }
            }
        }
    }
    
    public Object read( ModelProperty property )
    {
        property = property.refine( this );
        
        if( property == PROP_ID )
        {
            return getId();
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
        else if( property == PROP_WEIGHT )
        {
            return getWeight();
        }
        
        return super.read( property );
    }
    
    public void write( ValueProperty property, final Object value )
    {
        property = (ValueProperty) property.refine( this );
        
        if( property == PROP_ID )
        {
            setId( (String) value );
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
        else if( property == PROP_WEIGHT )
        {
            setWeight( (String) value );
            return;
        }
        
        super.write( property, value );
    }
    
}
