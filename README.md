# Inventory Plus (Fabric 1.21.1)
- +27 slots extra (3 filas) debajo del inventario normal, guardados con el jugador.
- Boton "Ordenar": junta stacks y ordena inventario principal + extra.
- Animacion en ola al abrir el inventario y al ordenar.
- Al morir sin keepInventory, los slots extra tambien sueltan sus items.
- Hay que instalarlo en cliente Y servidor (+ Fabric API).

## Compilar
**Opcion A (sin instalar nada):** sube esta carpeta a un repo de GitHub -> pestana Actions -> "build" -> descarga el artifact `inventoryplus-jar`.
**Opcion B (local):** JDK 21 + Gradle 8.8+, luego `gradle build` -> `build/libs/inventoryplus-1.0.0.jar`.
Probar: `gradle runClient`.
