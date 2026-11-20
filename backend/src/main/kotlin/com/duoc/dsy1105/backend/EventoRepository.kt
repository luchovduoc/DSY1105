package com.duoc.dsy1105.backend

import org.springframework.data.mongodb.repository.MongoRepository

// Spring genera solo guardar, buscar, listar y eliminar
interface EventoRepository : MongoRepository<Evento, String>
