1. Use [targetClassMap](https://github.com/TradeHero/routable/blob/tho/path_pattern_generation/route/src/main/java/com/tradehero/route/internal/RouterProcessor.java#L83-83) for marking a class (node) is visited.
    targetClassMap: **classElement -> routeInjector**

2. Visit class which is annotated with @Routable (question: in which order?)

3. Whenever visit a class, which is not visited before -->
        - visit super class if super class annotated with Routable (
        - mark as visited
        - parse and generate PATH_PATTERNS

Given a class (annotated with @Routable), how to get list of child fields annotated with 
@RouteProperty, for building a graph (question: can use tree?) like this: 

![image](https://cloud.githubusercontent.com/assets/1457567/5244782/b71b7214-798d-11e4-85df-5c746572dc04.png)
        
ClassNode

    - class element
    - routeInjector
    - generatedRoutes or PATH_PATTERNS <----- routeBinding

routeInjector

    --- field Binding ---> child ClassNodes (RouteProperty)
    --- annotated Routable super class ---> child ClassNodes (Routable)
