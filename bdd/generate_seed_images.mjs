// Genera bdd/seed_images.sql con las fotos de Aplicaciones-Grupo2-FE/fotostest
// asignadas a los productos del seed, como blobs JPEG (UPDATE ... SET foto = 0x...).
//
// Toma las imágenes YA re-encodeadas a JPEG del backend en marcha (perfil H2, productos 1..4,
// que el DataSeeder cargó desde fotostest). Así el blob queda en el formato que sirve el
// endpoint /api/productos/{id}/foto (image/jpeg).
//
// Uso (con el backend H2 corriendo en :8080):
//   node bdd/generate_seed_images.mjs
//
// Luego, contra MySQL:  source bdd/reset_seed_chocolateria_animales.sql ; source bdd/seed_images.sql
import { writeFileSync } from 'node:fs'
import { fileURLToPath } from 'node:url'
import { dirname, join } from 'node:path'

const BACKEND = process.env.BACKEND || 'http://localhost:8080'
const SOURCE_IDS = [1, 2, 3, 4] // productos H2 con foto1..foto4 de fotostest
const TOTAL_PRODUCTOS = 15 // productos que inserta reset_seed_chocolateria_animales.sql

async function getFoto(id) {
  const res = await fetch(`${BACKEND}/api/productos/${id}/foto`)
  if (!res.ok) throw new Error(`No se pudo leer la foto del producto ${id} (HTTP ${res.status})`)
  return Buffer.from(await res.arrayBuffer())
}

const fotos = []
for (const id of SOURCE_IDS) fotos.push(await getFoto(id))

let sql = '-- GENERADO por bdd/generate_seed_images.mjs — NO editar a mano.\n'
sql += '-- Asigna las fotos de fotostest (JPEG) a los productos del seed.\n'
sql += '-- Ejecutar DESPUÉS de reset_seed_chocolateria_animales.sql.\n\n'
for (let id = 1; id <= TOTAL_PRODUCTOS; id++) {
  const buf = fotos[(id - 1) % fotos.length]
  sql += `UPDATE producto SET foto = 0x${buf.toString('hex')} WHERE id = ${id};\n`
}

const outPath = join(dirname(fileURLToPath(import.meta.url)), 'seed_images.sql')
writeFileSync(outPath, sql)
console.log(`seed_images.sql escrito (${(sql.length / 1024 / 1024).toFixed(2)} MB) con ${TOTAL_PRODUCTOS} UPDATE, ${fotos.length} imágenes de fotostest.`)
