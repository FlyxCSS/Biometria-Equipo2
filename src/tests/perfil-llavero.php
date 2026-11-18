<?php
/** Pruebas de perfil y asignación con SQLite aislado. No prueba los bloqueos de MySQL. */
declare(strict_types=1);
require_once dirname(__DIR__) . '/business_logic/LogicaNegocio.php';
class BasePrueba extends PDO {
    public function __construct() { parent::__construct('sqlite::memory:', null, null, [PDO::ATTR_ERRMODE=>PDO::ERRMODE_EXCEPTION, PDO::ATTR_DEFAULT_FETCH_MODE=>PDO::FETCH_ASSOC]); }
    public function prepare(string $query, array $options = []): PDOStatement|false {
        return parent::prepare(str_replace(' FOR UPDATE', '', $query), $options);
    }
}
$db = new BasePrueba();
$db->exec('CREATE TABLE USUARIOS(id_usuario INTEGER PRIMARY KEY, foto_perfil TEXT);
    INSERT INTO USUARIOS VALUES(1,NULL),(2,NULL);
    CREATE TABLE LLAVEROS(id_llavero TEXT PRIMARY KEY, identificador_ble TEXT UNIQUE, id_parque INT, id_organizacion INT, estado INT, id_usuario INT UNIQUE);
    INSERT INTO LLAVEROS VALUES("uno","S-001",1,1,1,NULL),("dos","S-002",1,1,1,2),("tres","S-003",2,1,1,NULL),("cuatro","S-004",1,1,0,NULL);');
$l = new LogicaNegocio($db, []);
$u = ['id_usuario'=>1,'id_parque'=>1,'id_organizacion'=>1,'rol'=>'trabajador'];
$n = 0;
function verificar(bool $ok): void { global $n; if (!$ok) throw new RuntimeException('Prueba fallida '.($n+1)); $n++; }
function rechazo(Closure $operacion, int $estado): void {
    try { $operacion(); } catch (ErrorApi $e) { verificar($e->estado === $estado); return; }
    throw new RuntimeException('Se aceptó una operación inválida.');
}
foreach ([['foto_perfil'=>123], ['foto_perfil'=>'data:image/png;base64,AA=='], ['foto_perfil'=>'data:image/jpeg;base64,incorrecto'], ['foto_perfil'=>null,'id_usuario'=>2], []] as $datos) {
    rechazo(fn()=> $l->actualizarFotoPerfil($u,$datos),422);
}
$foto = 'data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEAYABgAAD/2wBDAAMCAgMCAgMDAwMEAwMEBQgFBQQEBQoHBwYIDAoMDAsKCwsNDhIQDQ4RDgsLEBYQERMUFRUVDA8XGBYUGBIUFRT/2wBDAQMEBAUEBQkFBQkUDQsNFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBT/wAARCAACAAIDASIAAhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQAAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSExBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEAPwD8qqKKKAP/2Q==';
verificar($l->actualizarFotoPerfil($u,['foto_perfil'=>$foto]) === ['foto_perfil'=>$foto]);
verificar($db->query('SELECT foto_perfil FROM USUARIOS WHERE id_usuario=1')->fetchColumn() === $foto);
$db->exec("UPDATE USUARIOS SET foto_perfil='anterior' WHERE id_usuario=2");
verificar($l->actualizarFotoPerfil($u,['foto_perfil'=>null]) === ['foto_perfil'=>null]);
verificar($db->query('SELECT foto_perfil FROM USUARIOS WHERE id_usuario=2')->fetchColumn() === 'anterior');
foreach (['S-002'=>409,'S-003'=>404,'S-004'=>404,'S-999'=>404,'mal'=>422] as $sensor=>$estado) {
    rechazo(fn()=> $l->vincularLlavero($u,['nombre_micro'=>$sensor]),$estado);
    verificar(!$db->inTransaction());
}
rechazo(fn()=> $l->vincularLlavero(array_merge($u,['rol'=>'jefe_parque']),['nombre_micro'=>'S-001']),403);
verificar($l->vincularLlavero($u,['nombre_micro'=>'S-001']) === ['llavero'=>'S-001']);
verificar((int)$db->query("SELECT id_usuario FROM LLAVEROS WHERE identificador_ble='S-001'")->fetchColumn() === 1);
verificar($l->vincularLlavero($u,['nombre_micro'=>'S-001']) === ['llavero'=>'S-001']);
rechazo(fn()=> $l->vincularLlavero($u,['nombre_micro'=>'S-002']),409);
verificar($l->desvincularLlavero($u) === ['llavero'=>null]);
verificar($db->query("SELECT id_usuario FROM LLAVEROS WHERE identificador_ble='S-001'")->fetchColumn() === null);
verificar((int)$db->query("SELECT id_usuario FROM LLAVEROS WHERE identificador_ble='S-002'")->fetchColumn() === 2);
verificar($l->desvincularLlavero($u) === ['llavero'=>null]);
echo "$n comprobaciones de perfil y llavero correctas (SQLite; sin concurrencia MySQL).\n";
