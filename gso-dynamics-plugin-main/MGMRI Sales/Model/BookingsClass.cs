﻿using Microsoft.Xrm.Sdk;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace MGM.Model
{
    public class BookingsClass
    {
        EntityReference Account { get; set; }
        EntityReference Agency { get; set; }
    }
}
