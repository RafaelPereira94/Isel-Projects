//------------------------------------------------------------------------------
// <auto-generated>
//     This code was generated from a template.
//
//     Manual changes to this file may cause unexpected behavior in your application.
//     Manual changes to this file will be overwritten if the code is regenerated.
// </auto-generated>
//------------------------------------------------------------------------------

namespace Aenima
{
    using System;
    using System.Collections.Generic;
    
    public partial class ExtraTime
    {
        public int id { get; set; }
        public int extra_time { get; set; }
    
        public virtual Promotion Promotion { get; set; }
    }
}
