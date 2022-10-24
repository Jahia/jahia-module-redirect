package org.jahiacommunity.modules.jexperience.redirect;

import org.jahia.api.Constants;
import org.jahia.services.SpringContextSingleton;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.filter.AbstractFilter;
import org.jahia.services.render.filter.RenderChain;
import org.jahia.services.render.filter.RenderFilter;
import org.jahia.services.seo.urlrewrite.UrlRewriteService;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.servlet.ServletException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

@Component(service = RenderFilter.class)
public class RedirectComponentFilter extends AbstractFilter {
    private static final Logger logger = LoggerFactory.getLogger(RedirectComponentFilter.class);

    public RedirectComponentFilter() {
        setApplyOnNodeTypes("jcnt:redirectComponent");
        setApplyOnModes("preview,live");
        setPriority(15.9f);
        setApplyOnTemplateTypes("html");
    }

    @Override
    public String prepare(RenderContext renderContext, Resource resource, RenderChain chain) {
        try {
            if (!resource.getNode().hasProperty("page")) {
                return null;
            }
            JCRNodeWrapper pageNode = (JCRNodeWrapper) resource.getNode().getProperty("page").getNode();
            if (pageNode == null || !pageNode.isNodeType(Constants.JAHIANT_PAGE)) {
                return null;
            }
            if (logger.isDebugEnabled()) {
                logger.debug("Redirect to {}...", pageNode.getUrl());
            }
            renderContext.setRedirect(
                    ((UrlRewriteService) SpringContextSingleton.getBean("UrlRewriteService"))
                            .rewriteOutbound(pageNode.getUrl(),
                                    renderContext.getRequest(), renderContext.getResponse()));
            return "";
        } catch (RepositoryException | ServletException | IOException | InvocationTargetException e) {
            logger.error("", e);
        }
        return null;
    }
}
