**MiniInventario - Gestión Inteligente para Pequeños Negocios**  
*(Proyecto Académico - Solución Integral de Inventarios Móviles)*  

---

### **Descripción Técnica**  
Aplicación Android diseñada para simplificar la gestión de inventarios en pequeños comercios, combinando:  
- **Control de stock en tiempo real**  
- **Clasificación por categorías** (Alimentos, Objetos)  
- **Alertas preventivas** (Vencimientos, Reposiciones)  
- **Análisis comercial básico** (Gráfico de ganancias)  

**Núcleo Funcional**:  
```java
// Gestión dinámica del inventario con Firebase
db.collection("productos")
  .whereEqualTo("id_negocio", negocio)
  .whereEqualTo("producto", nombreProducto)
  .get() // Consulta filtrada por negocio y producto
```

---

### **Características Destacadas**  
| Módulo | Tecnologías | Función |  
|--------|-------------|---------|  
| **Inventario Principal** | RecyclerView, Firestore | Listado interactivo con búsqueda en tiempo real |  
| **Gestión de Vencimientos** | Firebase Queries | Alertas visuales para productos próximos a caducar |  
| **Control de Clientes** | Activity Personalizada | Registro de compradores frecuentes |  
| **Análisis Comercial** | Gráficos Personalizados | Visualización de tendencias de ventas |  
| **Sincronización Multi-dispositivo** | Firebase Firestore | Acceso colaborativo desde múltiples usuarios |  

---

### **Arquitectura Clave**  
- **Frontend**:  
  - Navegación por ImageButtons intuitivos  
  - Diseño adaptable con ConstraintLayout  
  - Búsqueda predictiva (SearchView con filtrado Firestore)  

- **Backend**:  
  - Estructura NoSQL en Firestore (Colección `productos`)  
  - Modelo de datos relacional (`id_negocio` como clave foránea)  
  - Gestión de estados con RecyclerView Adapter personalizado  

---

### **Equipo de Desarrollo**  
**Colaboración Multidisciplinaria**:  
- **Gonzalo Morales**: Implementación Firebase  
- **Cristoffer Gatica**: Lógica de Negocios  
- **Daniel Uribe**: Diseño de Interfaz  
- **Alexis Gallegos**: Sistema de Notificaciones  
- **Vicente Scheihing**: Arquitectura General  

*"Proyecto desarrollado bajo metodología ágil con rotación de roles para aprendizaje integral"*

---

### **Logros Técnicos**  
- Implementación completa en **6 semanas** académicas  
- Integración exitosa de **5 módulos independientes**  
- Optimización de consultas Firestore con filtros compuestos  
- Sistema de layout dinámico (ajuste automático de RecyclerView)  

---

### **Propósito Educativo**  
Este proyecto demuestra nuestra capacidad para:  
1. Implementar CRUD complejo en entornos móviles  
2. Diseñar arquitecturas escalables para PYMES  
3. Trabajar colaborativamente con control de versiones  
4. Priorizar funcionalidad sobre estética en MVP  

**Tecnologías Dominadas**:  
`Firestore · RecyclerView · Material Design · Android Lifecycle`

---
  
**Video Demostrativo**: (https://youtu.be/MXmNSWYh_KM?si=IacVQ-RX-cNFrcxS)

*Nota: Proyecto académico demostrativo - Requiere mejoras de seguridad y escalabilidad para uso comercial*
