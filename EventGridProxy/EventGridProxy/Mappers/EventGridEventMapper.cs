// --------------------------------------------------------------------------------------------------------------------
// <copyright file="EventGridEventMapper.cs" company="MGM Resorts International">
// Copyright (c) 2021 MGM Resorts International. All rights reserved.
// </copyright>
// <author>MGM Resorts International</author>
// <summary>Implements the proxy routes configuration model class.</summary>
// --------------------------------------------------------------------------------------------------------------------

namespace Mgm.Sre.Services.EventGridProxy.Mappers
{
    using Azure.Messaging.EventGrid;
    using EventGridProxy.Models.Dto;
    using Newtonsoft.Json;

    /// <summary>
    /// The Event Grid Event Mapper.
    /// </summary>
    public static class EventGridEventMapper
    {
        /// <summary>
        /// Maps EventGridEvent to EventGridEventDto.
        /// </summary>
        /// <param name="eventGridEvent">The event Grid Event content.</param>
        /// <returns>EventGridEventDto.</returns>
        public static EventGridEventDto ToDto(this EventGridEvent eventGridEvent)
        {
            object eventDataObject = null;
            if (eventGridEvent.Data != null)
            {
                try
                {
                    // Deserialize BinaryData to a dictionary or dynamic object
                    eventDataObject = JsonConvert.DeserializeObject<Dictionary<string, object>>(eventGridEvent.Data.ToString());
                }
                catch (Exception)
                {
                    eventDataObject = eventGridEvent.Data.ToString(); // Fallback to raw string
                }
            }

            return new EventGridEventDto
            {
                Id = eventGridEvent.Id,
                EventType = eventGridEvent.EventType,
                Subject = eventGridEvent.Subject,
                EventTime = eventGridEvent.EventTime,
                Data = eventDataObject,
                DataVersion = eventGridEvent.DataVersion,
                Topic = eventGridEvent.Topic
            };
        }
    }
}