package com.gempukku.gaming.spawn;

import com.gempukku.gaming.asset.component.NameComponentManager;
import com.gempukku.gaming.asset.prefab.PrefabManager;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.Component;
import com.gempukku.secsy.entity.EntityManager;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.io.EntityData;
import com.gempukku.secsy.serialization.ComponentInformation;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.StringReader;
import java.util.Map;

@RegisterSystem(
        profiles = "entitySpawner",
        shared = EntitySpawner.class
)
public class EntitySpawnerSystem implements EntitySpawner {
    @Inject
    private PrefabManager prefabManager;
    @Inject
    private EntityManager entityManager;
    @Inject
    private NameComponentManager nameComponentManager;

    @Override
    public EntityRef spawnEntityFromRecipe(String recipe) {
        int indexOfBracket = recipe.indexOf("{");
        if (indexOfBracket == -1) {
            return spawnEntity(recipe, null);
        } else {
            String prefabName = recipe.substring(0, indexOfBracket);
            String recipeData = recipe.substring(indexOfBracket);

            JSONParser parser = new JSONParser();
            try {
                Map<String, Object> jsonData = (Map<String, Object>) parser.parse(new StringReader(recipeData));

                return spawnEntity(prefabName, jsonData);
            } catch (IOException | ParseException e) {
                throw new SpawnException("Unable to spawn entityJson", e);
            }
        }
    }

    @Override
    public EntityRef spawnEntity(String prefabName, Map<String, Object> changes) {
        EntityData entityData = prefabManager.getPrefabByName(prefabName);

        if (changes != null) {
            ModifiedEntityData result = new ModifiedEntityData(entityData);

            for (Map.Entry<String, Object> componentEntry : changes.entrySet()) {
                String componentName = componentEntry.getKey();
                Map<String, Object> componentData = (Map<String, Object>) componentEntry.getValue();
                if (componentName.startsWith("-")) {
                    componentName = componentName.substring(1);
                    removeComponent(result, componentName);
                } else if (componentName.startsWith("+")) {
                    componentName = componentName.substring(1);
                    addComponent(result, componentData, componentName);
                } else {
                    boolean optional = false;
                    if (componentName.startsWith("?")) {
                        componentName = componentName.substring(1);
                        optional = true;
                    }
                    modifyComponent(result, componentData, componentName, optional);
                }
            }

            entityData = result;
        }

        return entityManager.createEntity(entityData);
    }

    private void modifyComponent(ModifiedEntityData result, Map<String, Object> componentData, String componentName, boolean optional) {
        ModifiedComponentData modifiedComponentData = result.modifyComponent(getComponentByName(componentName));
        if (modifiedComponentData != null) {
            for (String fieldName : componentData.keySet()) {
                if (fieldName.startsWith("-")) {
                    fieldName = fieldName.substring(1);
                    if (!modifiedComponentData.removeField(fieldName))
                        throw new SpawnException("Unable to remove field from component: " + fieldName + ", " + componentName);
                } else {
                    Object fieldValue = componentData.get(fieldName);
                    modifiedComponentData.addField(fieldName, fieldValue);
                }
            }
        } else if (!optional) {
            throw new SpawnException("Unable to find component to modify in original prefab: " + componentName);
        }
    }

    private void addComponent(ModifiedEntityData result, Map<String, Object> componentData, String componentName) {
        Class<? extends Component> componentToAdd = getComponentByName(componentName);

        if (result.containsComponent(componentToAdd))
            throw new SpawnException("Original prefab already contains component to add: " + componentName);

        ComponentInformation componentInformation = new ComponentInformation(componentToAdd);
        for (String fieldName : componentData.keySet()) {
            Object fieldValue = componentData.get(fieldName);
            componentInformation.addField(fieldName, fieldValue);
        }
        result.addComponent(componentInformation);
    }

    private void removeComponent(ModifiedEntityData result, String componentName) {
        Class<? extends Component> componentToRemove = getComponentByName(componentName);
        if (!result.removeComponent(componentToRemove))
            throw new SpawnException("Unable to find component to remove in original prefab: " + componentName);
    }

    private Class<? extends Component> getComponentByName(String componentName) {
        Class<? extends Component> componentByName = nameComponentManager.getComponentByName(componentName);
        if (componentByName == null)
            throw new SpawnException("Unable to find component with name (found in prefab): " + componentName);
        return componentByName;
    }
}
