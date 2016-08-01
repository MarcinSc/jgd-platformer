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
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.StringReader;

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
    public EntityRef spawnEntity(String recipe) {
        EntityData entityData;
        int indexOfBracket = recipe.indexOf("{");
        if (indexOfBracket == -1) {
            entityData = prefabManager.getPrefabByName(recipe);
        } else {
            String prefabName = recipe.substring(0, indexOfBracket);
            String recipeData = recipe.substring(indexOfBracket);
            EntityData prefab = prefabManager.getPrefabByName(prefabName);

            ModifiedEntityData result = new ModifiedEntityData(prefab);

            JSONParser parser = new JSONParser();
            JSONObject entityJson = null;
            try {
                entityJson = (JSONObject) parser.parse(new StringReader(recipeData));

                for (String componentName : (Iterable<String>) entityJson.keySet()) {
                    if (componentName.startsWith("-")) {
                        componentName = componentName.substring(1);
                        removeComponent(result, componentName);
                    } else if (componentName.startsWith("+")) {
                        componentName = componentName.substring(1);
                        addComponent(result, entityJson, componentName);
                    } else {
                        boolean optional = false;
                        if (componentName.startsWith("?")) {
                            componentName = componentName.substring(1);
                            optional = true;
                        }
                        modifyComponent(result, entityJson, componentName, optional);
                    }
                }
            } catch (IOException | ParseException e) {
                throw new SpawnException("Unable to spawn entityJson", e);
            }
            entityData = result;
        }
        return entityManager.createEntity(entityData);
    }

    private void modifyComponent(ModifiedEntityData result, JSONObject entityJson, String componentName, boolean optional) {
        ModifiedComponentData modifiedComponentData = result.modifyComponent(getComponentByName(componentName));
        if (modifiedComponentData != null) {
            JSONObject componentObject = (JSONObject) entityJson.get(componentName);
            for (String fieldName : (Iterable<String>) componentObject.keySet()) {
                if (fieldName.startsWith("-")) {
                    fieldName = fieldName.substring(1);
                    if (!modifiedComponentData.removeField(fieldName))
                        throw new SpawnException("Unable to remove field from component: " + fieldName + ", " + componentName);
                } else {
                    Object fieldValue = componentObject.get(fieldName);
                    modifiedComponentData.addField(fieldName, fieldValue);
                }
            }
        } else if (!optional) {
            throw new SpawnException("Unable to find component to modify in original prefab: " + componentName);
        }
    }

    private void addComponent(ModifiedEntityData result, JSONObject entityJson, String componentName) {
        Class<? extends Component> componentToAdd = getComponentByName(componentName);

        if (result.containsComponent(componentToAdd))
            throw new SpawnException("Original prefab already contains component to add: " + componentName);

        ComponentInformation componentInformation = new ComponentInformation(componentToAdd);
        JSONObject componentObject = (JSONObject) entityJson.get(componentName);
        for (String fieldName : (Iterable<String>) componentObject.keySet()) {
            Object fieldValue = componentObject.get(fieldName);
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
