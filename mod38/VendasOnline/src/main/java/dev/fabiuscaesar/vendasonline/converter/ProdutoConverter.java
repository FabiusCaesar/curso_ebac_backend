/**
 * 
 */
package dev.fabiuscaesar.vendasonline.converter;

import java.util.HashMap;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import dev.fabiuscaesar.vendasonline.domain.Produto;

/**
 * Converte entre a String do componente e o objeto Produto.
 * Estratégia: mapeia os objetos em um Map dentro do ViewMap da página, evitando hits no banco.
 * 
 * @author FabiusCaesar
 * @date 17 de out. de 2025
 */

@FacesConverter(value = "produtoConverter", managed = true)
public class ProdutoConverter implements Converter<Produto> {

    private static final String VIEW_KEY = ProdutoConverter.class.getName();

    @Override
    public Produto getAsObject(FacesContext ctx, UIComponent comp, String key) {
        if (key == null || key.isBlank()) return null;
        Object obj = getStore(ctx).get(key);
        return (obj instanceof Produto) ? (Produto) obj : null;
    }

    @Override
    public String getAsString(FacesContext ctx, UIComponent comp, Produto value) {
        if (value == null) return "";
        // use uma chave estável quando possível (id), com fallback temporário se ainda não persistido
        String k = (value.getId() != null) ? ("id:" + value.getId()) : ("tmp:" + System.identityHashCode(value));
        getStore(ctx).put(k, value);
        return k;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getStore(FacesContext ctx) {
        Map<String, Object> viewMap = ctx.getViewRoot().getViewMap();
        return (Map<String, Object>) viewMap.computeIfAbsent(VIEW_KEY, _k -> new HashMap<>());
    }
}
