/*
 * INSTITUTO SUPERIOR DE ENGENHARIA DE LISBOA
 * Licenciatura em Engenharia Informática e de Computadores
 *
 * Programação Concorrente - Inverno de 2009-2010
 * Paulo Pereira
 *
 * Código base para a 3ª Série de Exercícios.
 *
 */

using System.Collections.Generic;
using System.Linq;
using System.Net;

namespace Tracker
{
    /// <summary>
    /// Singleton class that contains information regarding the tracked files, namely, the files' names
    /// and locations.
    /// 
    /// NOTE: This implementation is not thread-safe.
    /// </summary>
    public class Store
    {
        /// <summary>
        /// The singleton instance.
        /// </summary>
        private static readonly Store _instance = new Store();

        /// <summary>
        /// Gets the singleton instance.
        /// </summary>
        public static Store Instance
        {
            get { return _instance; }
        }

        #region Instance members

        /// <summary>
        /// The dictionary instance that holds the tracked files information.
        /// </summary>
        private readonly Dictionary<string, HashSet<IPEndPoint>> _store;

        /// <summary>
        /// Empty array instance used to report the absence of tracking information for a particular file.
        /// </summary>
        private readonly IPEndPoint[] noLocations;

        /// <summary>
        /// Initiates the store instance.
        /// </summary>
        private Store()
        {
            _store = new Dictionary<string, HashSet<IPEndPoint>>();
            noLocations = new IPEndPoint[0];
        }

        /// <summary>
        /// Registers the given file as being hosted at the given location. 
        /// </summary>
        /// <param name="fileName">The file's name.</param>
        /// <param name="client">The file's location.</param>
        public void Register(string fileName, IPEndPoint client)
        {
            HashSet<IPEndPoint> fileHosts = null;
            if (!_store.ContainsKey(fileName))
                _store[fileName] = (fileHosts = new HashSet<IPEndPoint>());
            else
                fileHosts = _store[fileName];
            fileHosts.Add(client);
        }

        /// <summary>
        /// Removes the given location for the given file (if both exist).
        /// </summary>
        /// <param name="fileName">The file's name.</param>
        /// <param name="client">The location of the hosting client.</param>
        /// <returns>A boolean value indicating if the file's location as been unregistered successfully.</returns>
        public bool Unregister(string fileName, IPEndPoint client)
        {
            // Is file being tracked?
            if (!_store.ContainsKey(fileName))
                return false;

            // File locations are being tracked. Unregister client location.
            HashSet<IPEndPoint> locations = _store[fileName];
            bool result = locations.Remove(client);

            if (result && locations.Count == 0)
                // Last client hosting the tracked file. Remove it from the store.
                _store.Remove(fileName);

            return result;
        }

        /// <summary>
        /// Gets the names of the files currently being tracked.
        /// </summary>
        /// <returns>An array with the tracked files' names.</returns>
        public string[] GetTrackedFiles()
        {
            return _store.Keys.ToArray();
        }

        /// <summary>
        /// Gets the locations of the given file.
        /// </summary>
        /// <param name="fileName">The file's name.</param>
        /// <returns>An array with the tracked files' locations.</returns>
        public IPEndPoint[] GetFileLocations(string fileName)
        {
            IPEndPoint[] locations = noLocations;
            if (_store.ContainsKey(fileName))
                locations = _store[fileName].ToArray();
            return locations;
        }

        #endregion
    }
}
