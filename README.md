# Dynamic Name Tags
This mod adds dynamic name tags.

For example, naming a mob "Dinnerbone" or "Grumm" also causes its name to be upside down. Naming a sheep "jeb_" makes its name cycle through the sheep colors.

## Developers
To add your own Dynamic Name Tag™, you will have to call `DynamicNameTagsRegistry.register(String entityName, EntityFunction function)`. `EntityFunction` is a functional interface, meaning you can use a lambda as the second parameter.

For example, here's how you would go to make the name tag of any players named `pr_ib` blue:

```java
DynamicNameTagsRegistry.register("pr_ib", (entity, text, matrices, vertexConsumers, light, dispatcher) -> {
    if (!(entity instanceof PlayerEntity)) return text; // This makes sure that you are only affecting players
    return text.copy().setStyle(Style.EMPTY.withFormatting(Formatting.BLUE));
});
```

Note that the registry is never frozen, meaning you can register a dynamic name at any point. For instance, if you wanted, you could make it so that defeating the Ender Dragon registers a new dynamic name.

If you wish to remove a dynamic name, you can do so with `DynamicNameTagsRegistry.remove(String entityName)`. Here's how you would remove the previous example:

```java
DynamicNameTagsRegistry.remove("pr_ib");
```