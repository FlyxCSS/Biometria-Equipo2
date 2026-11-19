<?php
/*
 * Nombre: historial.php
 * Descripción: Comprobación automática de contratos, límites y aislamiento.
 * Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
 * Fecha: 19/11/2026
 * Autores: Fabián Andrés Useche Rivera; Élia Garcia Pons; Judith Civico Aporta; Julia Valen de Oliveira; Soufiane Belfquih El Khalfi
 * Aportación: Comprobación automática de contratos, límites y aislamiento.
 */

declare(strict_types=1);
require_once dirname(__DIR__) . '/communication/ContextoWeb.php';
require_once dirname(__DIR__).'/business_logic/LogicaNegocio.php';
$db=new PDO('sqlite::memory:',null,null,[PDO::ATTR_ERRMODE=>PDO::ERRMODE_EXCEPTION,PDO::ATTR_DEFAULT_FETCH_MODE=>PDO::FETCH_ASSOC]);
$db->exec('CREATE TABLE MEDICIONES(fecha_hora TEXT,id_llavero TEXT,nombre_micro TEXT,valor INT,unidad TEXT,latitud REAL,longitud REAL,simulada INT,id_usuario INT,id_parque INT)');
$q=$db->prepare('INSERT INTO MEDICIONES VALUES(?,?,?,?,?,?,?,?,?,?)');
for($i=1;$i<=45;$i++) $q->execute(['2026-11-19 10:00:00.000',sprintf('S-%03d',$i),sprintf('S-%03d',$i),42,'ppb',39,-.1,0,1,1]);
$q->execute(['2026-11-19 10:00:00.000','OTHER','OTHER',99,'ppb',39,-.1,0,2,1]);
$l=new LogicaNegocio($db,[]);$u=['rol'=>'trabajador','id_usuario'=>1,'id_parque'=>1];$seen=[];$cursor=null;$n=0;
do {$page=$l->historialMediciones($u,$cursor);foreach($page['mediciones'] as $m){if(isset($seen[$m['nombre_micro']])||$m['nombre_micro']==='OTHER')throw new RuntimeException('Aislamiento o duplicación');$seen[$m['nombre_micro']]=true;}$cursor=$page['siguiente'];$n++;}while($cursor!==null);
if(count($seen)!==45||$n!==3)throw new RuntimeException('Paginación incompleta');
foreach(['invalid',base64_encode('[1,2]'),str_repeat('a',1025)] as $bad){try{$l->historialMediciones($u,$bad);throw new RuntimeException('Cursor aceptado');}catch(ErrorDominio $e){if(estadoErrorDominio($e)!==422)throw $e;}}
try{$l->historialMediciones(array_merge($u,['rol'=>'jefe_parque']));throw new RuntimeException('Rol aceptado');}catch(ErrorDominio $e){if(estadoErrorDominio($e)!==403)throw $e;}
echo "Historial: 45 lecturas, 3 páginas, aislamiento de cuenta, cursores inválidos y rol correctos.\n";
