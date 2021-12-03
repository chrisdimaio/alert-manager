* Bulk event send method. Caches events until a trigger amount or time or both is reached.
** cuts down on communication.
** probably only makes sense if reply not needed.
** auditing with cookie crumbs is a good first spot for this.
* Auditing of every hop with cookie crumb as id.
** audit verticle will log all cookie crumbs
** audit trails could be visualized.