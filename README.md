# Dynamic Name Tags
This mod adds dynamic name tags.

For example, naming a mob "Dinnerbone" or "Grumm" also causes its name to be upside down. Naming a sheep "jeb_" makes its name cycle through the sheep colors.

## Developers
To add your own Dynamic Name Tag™, you need to create a resource pack JSON file located at `modid:dynamic_name_tags/<file>.json`. The name does not matter for most purposes, unless you want to replace an existing dynamic name tag.

Here are the parameters for the JSON file:

| Parameter        | Description                                                                                                                                                                                           |
|------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `matches`        | A regex which determines what names should be affected. For instance, `jeb_` would match any entity with that name tag, `^cat.*dog$` would match name tags starting with "cat" and ending with "dog." |
| `predicate`      | An entity predicate. Works the same as the one in the `minecraft:entity_properties` loot condition, with the difference that it does not support type-specific conditions or a structure predicate.   |
| `text`           | The text to be replaced with. If it is a literal text, the original name of the entity is passed as one of the arguments.                                                                             |
| `text_functions` | A text function to be applied to the text. See below for a list of text functions.                                                                                                                    |

For example, here is the [`jeb.json`](src/client/generated/assets/dynamicnametags/dynamic_name_tags/jeb.json) file:

```json
{
  "matches": "jeb_",
  "predicate": {
    "type": "minecraft:sheep"
  },
  "text": {
    "translate": "dynamicnametags.literal"
  },
  "text_functions": [
    "dynamicnametags:jeb"
  ]
}
```

Instead of manually writing the JSON files, you can also use [`AbstractDynamicNameTagProvider`](src/client/java/net/pedroricardo/datagen/AbstractDynamicNameTagProvider.java) in your data generator.

### List of text functions
| ID                       | Description                                                  |
|--------------------------|--------------------------------------------------------------|
| `dynamicnametags:jeb`    | Makes the text cycle through dye colors like a `jeb_` sheep. |
| `dynamicnametags:damage` | Makes the text more red the more damaged the entity is.      |

### Registering your own text function
You can register text functions in the registry located at [`TextFunctionRegistry`](src/client/java/net/pedroricardo/content/TextFunctionRegistry.java).