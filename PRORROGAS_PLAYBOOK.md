# Playbook: implementar PRÓRROGAS en un proyecto estilo TRANSFERENCIAS (p. ej. RENOVACIÓN)

Este documento resume, paso a paso, cómo se implementó la funcionalidad **PRÓRROGAS** en el
proyecto de modificaciones al registro, para replicarla en un proyecto cuya estructura es
**similar a TRANSFERENCIAS** (tablas separadas por estado: `notificacion`, `abandono`,
`desistimiento`, `caducada`, ... en lugar de una sola tabla con `tipo_estado`).

> Reemplaza los nombres `Renovacion*` / `renovacion` por los reales del proyecto destino.
> Al abrir Claude Code en ese proyecto, podrá leer el código real y afinar los nombres exactos.

---

## 0. Concepto del flujo

> **Nota (jul-2026):** la dirección requirente pidió mover la opción a **ABANDONOS** (es ahí donde
> el analista determina la procedencia de la prórroga) y que el paso sea **inmediato**, no diferido
> por scheduler. Lo que sigue describe el flujo vigente.

1. En la pantalla de **ABANDONOS**, el usuario **selecciona** registros y pulsa **PARA PRÓRROGA**.
2. Se abre un **diálogo** con los seleccionados; permite indicar **días de prórroga**
   (default **10**) y, **por fila**, `numero_alcance` (No. de escrito) y `fecha_alcance`.
3. Al confirmar, cada trámite pasa **inmediatamente** al estado de prórroga, fijando
   `fecha_puesta_prorroga = hoy`, `dias_prorroga`, `fecha_prorroga = hoy` y `numero_prorroga`
   (secuencia anual). En transferencias se **copia** a la tabla `prorroga` y se **elimina** de
   `abandono`; en los módulos de tabla única basta con `tipo_estado = 'PRORROGA'`.
4. La pestaña **PRÓRROGAS** cuenta el plazo desde `fecha_puesta_prorroga` en **días hábiles**.
   Mientras está en plazo la fila se resalta en **ámbar**; al vencer pasa a **rojo**, el tooltip
   indica los días vencidos y aparece un **banner de alerta** con el total de prórrogas vencidas.
5. Con esa alerta, el analista abre **EDITAR → PASAR A** y remite el trámite a **Certificados**,
   **Notificaciones** o **Abandonos**.
6. Al **notificar** el PDF de la prórroga (subida de certificados), se marca
   `prorroga_notificada = true`; en la pestaña, el **número de prórroga** se vuelve un **enlace**
   al PDF notificado.

**Reglas de negocio:**
- `numero_prorroga`: **secuencia anual incremental**, obligatoria; se asigna al pasar a prórroga
  (o al guardar/editar si viniera nula).
- No se puede pasar a prórroga un trámite cuya solicitud ya exista en prórroga.
- Para **ver el PDF** son indispensables `numero_prorroga`, `numero_alcance` y `fecha_alcance`
  (si faltan, se lanza aviso y no se abre).
- Al remitir una prórroga a **Abandonos** se fija `fecha_abandono = hoy` y un `numero_abandono`
  nuevo. En transferencias el `tipo_abandono` queda vacío (la tabla `prorroga` no lo guarda) y
  el analista debe elegirlo al editar el abandono.

---

## 1. Base de datos (las crea el usuario)

### 1.1 Tabla nueva `prorroga`
Crear una tabla `prorroga` con **la misma forma que la tabla `abandono`** del proyecto (todos los
campos base: solicitud, fecha_presentacion, notificacion, fecha_notificacion, registro,
fecha_registro, denominacion, signo, titulares/partes, apoderado, casilleros, responsable,
identificacion, email, comprobante, certificado, certificado_emitido, notificacion_emitida,
cancelado, solicitante, ...) **más** las columnas de prórroga:

```sql
-- columnas propias de prórroga en la tabla dedicada
fecha_puesta_prorroga DATE,
fecha_prorroga        DATE,
prorroga_notificada   BOOLEAN,
dias_prorroga         INTEGER,
numero_prorroga       INTEGER,
numero_alcance        VARCHAR(255),
fecha_alcance         DATE
```

### 1.2 Columnas "pendiente" en la tabla de abandonos
En la tabla que hace de **ABANDONOS** (equivalente a `abandono` en transferencias). Sirven para
bindear el diálogo por fila y para arrastrar los datos al pasar a prórroga:

```sql
ALTER TABLE <tabla_abandonos>
    ADD COLUMN fecha_puesta_prorroga DATE,
    ADD COLUMN dias_prorroga         INTEGER,
    ADD COLUMN numero_alcance        VARCHAR(255),
    ADD COLUMN fecha_alcance         DATE;
```

En los módulos de **tabla única** (`tipo_estado`) no hace falta nada: son la misma tabla.

---

## 2. Entidades JPA

### 2.1 Entidad `Prorroga` (tabla `prorroga`)
Clonar la entidad `Abandono` (misma tabla-forma), renombrar `@Table(name="prorroga")`, clase
`Prorroga`, y añadir los campos + getters/setters:
`fechaPuestaProrroga`, `fechaProrroga`, `prorrogaNotificada` (Boolean), `diasProrroga` (Integer),
`numeroProrroga` (Integer), `numeroAlcance` (String), `fechaAlcance` (Date).
Registrar la clase en `persistence.xml` (mismo persistence-unit que las demás entidades del módulo).

### 2.2 Entidad de ABANDONOS
Añadir a la entidad de abandonos los 4 campos "pendiente" con sus getters/setters:
`fechaPuestaProrroga` (Date), `diasProrroga` (Integer), `numeroAlcance` (String),
`fechaAlcance` (Date).

---

## 3. DAOs

### 3.1 `ProrrogaDAO` (nuevo) — clonar de `AbandonoDAO`
Métodos:
- `buscarTodos()` → `SELECT n FROM Prorroga n ORDER BY n.id DESC` (maxResults 300)
- `validarExistenciaProrroga(Prorroga n)` → misma solicitud, id distinto
- `getProrrogaByCriteria(String text)` → LIKE por solicitud/denominacion/titular
- `getProrrogaBySolicitud(String solicitud)`
- `getProrrogaByFecha(Date ini, Date fin)`
- `getProrrogasByDenominacion(String denominacion)`
- `getProrrogaByTitular(String titular)`
- `getNextNumeroProrroga(Date fecha)` → `SELECT MAX(n.numeroProrroga) ...`, con reinicio por año
  (comparar `fecha.getYear()` vs el año de la última `fechaProrroga`).

---

## 4. Controlador (fachada) — wrappers
Agregar:
```
saveProrroga / updateProrroga / removeProrroga
getProrrogas() (todos)
getProrrogasByCriteria / getProrrogasByFecha / getProrrogasByDenominacion / getProrrogasByTitular
getProrrogaBySolicitud
getNextNumeroProrroga(Date)
validarExistenciaProrroga(Prorroga)
```
Patrón de save/update/remove idéntico a `saveAbandono/updateAbandono/removeAbandono`
(el remove hace `merge` si el entity no está managed).

---

## 5. Bean de ABANDONOS — botón "PARA PRÓRROGA"
Agregar:

- Campo `private Integer diasProrroga;` + getter/setter.
- `prepararParaProrrogas()`: valida selección no vacía, pone `diasProrroga = 10` y lanza el
  callback `proit=true` para abrir el diálogo.
- `paraProrrogas(ActionEvent)`: valida selección y `diasProrroga > 0`; por cada seleccionado
  (los `numeroAlcance`/`fechaAlcance` vienen bindeados por fila desde el diálogo):
  - **tabla única**: `setTipoEstado("PRORROGA")`, `setFechaPuestaProrroga(new Date())`,
    `setDiasProrroga(diasProrroga)`, `setFechaProrroga(new Date())`, `setNumeroProrroga(...)`
    si viene nulo, y `updateXxx(...)`;
  - **tablas separadas** (transferencias): verifica con `getProrrogaBySolicitud` que no exista ya,
    crea la `Prorroga` copiando todos los campos base, fija los de prórroga, `saveProrroga(...)`
    y `removeAbandono(...)`;
  - `saveHistorial(... "PASADO A PRÓRROGA (N DÍAS)")`, recarga y callback `proit=true`.

## 6. Página de ABANDONOS (.xhtml)
- Botón **PARA PRÓRROGA** sobre la tabla:
  `oncomplete="if(args.proit){PF('dlgParaProrroga').show();}"`, `update="mensajes dlgParaProrroga paraProForm"`.
- **Diálogo `dlgParaProrroga`** (form `paraProForm`): tabla de seleccionados
  (`#{bean.selectedAbandonos}`) con columnas Solicitud / Denominación / Abandono / F. Abandono /
  **Escrito No. (Alcance)** (inputText a `#{item.numeroAlcance}`) / **Fecha Escrito** (calendar a
  `#{item.fechaAlcance}`); debajo, input **Días de Prórroga** (`#{bean.diasProrroga}`, required);
  botones CERRAR y **PASAR A PRÓRROGA** (`actionListener=#{bean.paraProrrogas}`,
  `oncomplete="if(args.proit){PF('dlgParaProrroga').hide();}"`, y en `update` el id real de la
  tabla — `tablaDeDatos` o `tablaDeAbandonos` según la página).
- **Enlace de menú** PRÓRROGAS → `prorroga<modulo>.xhtml`.

---

## 7. Scheduler — sin intervención
El paso a prórroga es **inmediato**, así que el scheduler de abandonos **no** participa. Si vienes
de una versión que copiaba de notificadas a prórroga con `createProrrogas*()`, elimina esos métodos
y sus llamadas.

---

## 8. Pestaña PRÓRROGAS: bean + página
- **`ProrrogaRenBean`** (clon del bean de una pestaña listado, tipo `ProrrogaTransfBean`):
  `loadProrrogas()` = `c.getProrrogas()`, `buscarProrroga` = byCriteria, `buscarProrrogasPorFecha`
  = byFecha, `eliminarProrroga` = removeProrroga, `prepararEditar`/`guardarProrroga`
  (autoasigna `numeroProrroga` si viene null), `prepararHistorial`, `buscarCasillero`,
  `prepararExpediente`, `selectedProrrogas`, `viewProrroga`/`downloadSelected` (flotante en LoginBean),
  `validarProrroga(...)` (busca el PDF notificado por `getUploadNotificacionBySolicitud` filtrando
  `tipo` que contenga `PRORROGA`), y `faltanDatosAlcance(...)` (exige numeroProrroga + numeroAlcance + fechaAlcance).
- **`prorrogaren.xhtml`** (clon de la pestaña de listado):
  tabla con columnas + **N. Prórroga como enlace** cuando `prorrogaNotificada` (abre el PDF con
  `otherpage(args.view)`), botón **Descargar** por fila (ícono PDF → servlet), botón **Descargar
  Seleccionados** dentro del `<f:facet name="footer">` de la tabla (¡importante que esté dentro para
  que `process="tablaDeDatos"` lo procese!), diálogo de edición con `Escrito No.`/`Fecha Escrito`/
  `N. Prórroga` (readonly) y **Solicitante** (`required="true"`). Campo `numeroProrroga` de solo-lectura.
  Enlaces de menú PRÓRROGAS en todas las páginas del módulo.
- **Solicitante obligatorio**: además del `required` de la vista, `guardarProrroga` corta con aviso si
  viene vacío, y `faltanDatosAlcance(...)` lo exige junto a `numeroProrroga`/`numeroAlcance`/`fechaAlcance`
  para poder abrir el PDF (individual y múltiple).

### 8.1 Alerta de plazo vencido
En el bean: `isProrrogaVencida(p)` (hoy no es anterior al límite, en días laborables si es SENADI
o de corrido si es IEPI),
`getEstiloProrroga(p)` → `row-prorroga` / `row-prorroga-vencida`, `getTooltipProrroga(p)` (días que
faltan o que lleva vencida) y `calcularAlertaVencidas()` llamado en cada carga/búsqueda para llenar
`alertaVencidas`.
En la página: `rowStyleClass="#{bean.getEstiloProrroga(prorroga)}"`, `title="#{bean.getTooltipProrroga(prorroga)}"`
en Solicitud y Días, columna **Días Restantes** (`getDiasRestantes(p)` → "Faltan N días" / "Vence hoy" /
"Vencida hace N días", en ámbar o rojo según el estado), leyendas EN PLAZO / PLAZO VENCIDO, y un
`<p:outputPanel id="alerta_pro">` con el banner rojo (incluirlo en el `update` de las búsquedas, el
guardar y el eliminar).
CSS: `.row-prorroga-vencida { border: solid #f44336 !important; background: #ffebee !important; }`

### 8.2 Destino ABANDONO en "PASAR A"
El `selectOneMenu` de la edición ofrece NOTIFICADAS / CERTIFICADO / **ABANDONO**. En `guardarProrroga`:
- **tabla única**: `setTipoEstado("ABANDONO")`, `setFechaAbandono(new Date())` y `numeroAbandono` nuevo si es nulo;
- **tablas separadas**: crear el `Abandono` copiando los campos, `fechaAbandono = hoy`,
  `numeroAbandono = getNextNumeroAbandono(...)`, `saveAbandono` + `removeProrroga`.

---

## 9. Reporte (PDF) descargable
- **LoginBean**: flotante `Prorroga getProrroga()/setProrroga()` + `List<Prorroga> getProrrogas()/setProrrogas()`.
- **Servlet `InformeProrrogaRen`** (clon de `InformeAbandono`/`InformeProrrogaTransf`),
  `urlPatterns={"/prorrogarenreport"}`; individual (lb.isVarious()==false → un PDF) y múltiple
  (zip). Reutiliza la plantilla `.jrxml` genérica pasando el `tipo_mod` = nombre de la tabla
  (`"prorroga"`).
- El método de `Report.java` que llena el reporte es genérico (recibe `tipo_mod`); reutilizar el
  mismo que usa abandono (`viewAbandonoProrrogaAll` / `...MasterBytes`).
- **OJO plantilla**: el `.jrxml` compartido hace `SELECT a.* FROM <tipo_mod>`; si declara campos que
  la tabla `prorroga` no tiene, Jasper falla al generar. Solución: agregar esas columnas a `prorroga`
  o hacer un `.jrxml` dedicado. Verificar al probar el PDF.

---

## 10. Marcar `prorroga_notificada` al notificar el PDF
En el bean/flujo de **subida y notificación de certificados** (equivalente a `UploadCertBean`),
en la rama de este módulo, tras el chequeo de abandono, agregar:

```java
Prorroga proaux = c.getProrrogaBySolicitud(un.getSolicitud());
if (proaux.getId() != null) {
    proaux.setProrrogaNotificada(true);
    c.updateProrroga(proaux);
    c.saveHistorial("PRORROGA", "PRORROGA", proaux.getSolicitud(), "PRÓRROGA NOTIFICADA " + un.getDocumento(), 0, login.getNombre());
    un.setTipo("PRORROGA RENOVACION");
}
```
(Identifica por tabla: si la solicitud existe en `prorroga`, el documento notificado es la prórroga.
En los módulos de una sola tabla se usó detección por texto "ampliación al término"; en tablas
separadas basta con `getProrrogaBySolicitud`.)

---

## 11. Reporte de modificaciones (si el proyecto tiene un reporte consolidado)
Si existe un "reporte de modificaciones" que consolida estados: agregar `addProrroga(Prorroga)`
(estado "PRORROGA", numDocumento = numeroProrroga, fecha = fechaProrroga) y engancharlo en cada
ruta de búsqueda (por número, fecha, denominación, titular), consultando `getProrrogas...`.
Igual con abandono si faltara.

---

## 12. Presentación de diálogos (opcional, cosmético)
Clase CSS `dlgFormNice` (barra de título de color, contenido con mejor espaciado) aplicada como
`styleClass="dlgFormNice"` a los diálogos de formulario (`dlgNuevaT`). Se puede dar un color propio
por módulo con una clase extra (`dlgFormNice dlg-ren`) que sobreescriba
`.dlgFormNice.dlg-ren.ui-dialog .ui-dialog-titlebar { background: <color>; }` y el foco de inputs.

---

## Checklist de implementación

- [ ] BD: tabla `prorroga` (forma de `abandono` + 7 col. prórroga) + 4 col. "pendiente" en abandonos
- [ ] Entidad `Prorroga` + registro en `persistence.xml`
- [ ] 4 campos "pendiente" en entidad de abandonos
- [ ] `ProrrogaDAO`
- [ ] Wrappers en Controlador (save/update/remove/get.../numeración/validar)
- [ ] Bean abandonos: diasProrroga, prepararParaProrrogas, paraProrrogas (paso inmediato)
- [ ] Página abandonos: botón + diálogo + enlace de menú
- [ ] `ProrrogaRenBean` + `prorrogaren.xhtml` + enlaces de menú
- [ ] Alerta de vencimiento: estilo de fila, tooltip, banner
- [ ] Solicitante obligatorio para editar y para generar el PDF
- [ ] Destino ABANDONO en el "PASAR A" de la pestaña PRÓRROGAS
- [ ] LoginBean flotante `Prorroga` + servlet `InformeProrrogaRen`
- [ ] Marcar `prorroga_notificada` en el flujo de notificación de PDFs
- [ ] (opcional) reporte consolidado: addProrroga
- [ ] (opcional) estilo `dlgFormNice` con color
- [ ] Compilar y probar: abandono → para prórroga → ver en pestaña → alerta al vencer → remitir → PDF

---

### Notas clave aprendidas
- El plazo se calcula con `Operaciones.calcularFechaLimiteProrroga(solicitud, Date, int)`: en trámites
  **SENADI-XXXX-XXXX** cuenta **días laborables** (excluye sábados y domingos) y en trámites
  **IEPI-XXXX-XXXX** cuenta **días de corrido**. El discriminante es el prefijo de la solicitud
  (`Operaciones.esSolicitudIepi`), el mismo criterio con el que el reporte cambia de formato.
- Persistir cada registro antes de pasar al siguiente, así la numeración anual no se repite.
- El botón "Descargar Seleccionados" **debe ir dentro del `<f:facet name="footer">`** de la tabla
  para que `process="tablaDeDatos"` lo ejecute (si va fuera, no dispara la acción ni muestra mensajes).
- En Java no confundir `&&` con la entidad HTML (`&amp;&amp;`).
