document.addEventListener('DOMContentLoaded', () => {
    const defaultCenter = [45.8565, 9.3967];

    const mapOptions = (element) => ({
        tileUrl: element.dataset.tileUrl || 'https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png',
        attribution: element.dataset.attribution || '© OpenStreetMap contributors'
    });

    const addBaseLayer = (map, element) => {
        const options = mapOptions(element);
        L.tileLayer(options.tileUrl, {
            attribution: options.attribution,
            maxZoom: 19
        }).addTo(map);
    };

    const validCoordinate = (value, min, max) => {
        const number = Number(value);
        return Number.isFinite(number) && number >= min && number <= max;
    };

    const jobsMap = document.getElementById('jobs-map');
    if (jobsMap && window.L) {
        const markers = Array.from(document.querySelectorAll('.job-card'))
            .map(card => ({
                latitude: card.dataset.latitude,
                longitude: card.dataset.longitude,
                title: card.querySelector('h3')?.textContent || 'Lavoro',
                id: card.querySelector('a[href*="/lavori/"]')?.href
            }))
            .filter(job => validCoordinate(job.latitude, -90, 90) && validCoordinate(job.longitude, -180, 180));

        if (markers.length) {
            const map = L.map(jobsMap).setView([Number(markers[0].latitude), Number(markers[0].longitude)], 13);
            addBaseLayer(map, jobsMap);
            const bounds = [];
            markers.forEach(job => {
                const point = [Number(job.latitude), Number(job.longitude)];
                bounds.push(point);
                L.marker(point).addTo(map).bindPopup(
                    `<strong>${escapeHtml(job.title)}</strong><br><a href="${job.id}">Apri dettaglio</a>`
                );
            });
            if (bounds.length > 1) map.fitBounds(bounds, { padding: [24, 24] });
        } else {
            jobsMap.hidden = true;
            document.getElementById('jobs-map-empty')?.removeAttribute('hidden');
        }
    }

    const detailMap = document.getElementById('job-detail-map');
    if (detailMap && window.L) {
        const latitude = Number(detailMap.dataset.latitude);
        const longitude = Number(detailMap.dataset.longitude);
        const map = L.map(detailMap).setView([latitude, longitude], 15);
        addBaseLayer(map, detailMap);
        L.marker([latitude, longitude]).addTo(map).bindPopup(escapeHtml(detailMap.dataset.title || 'Lavoro')).openPopup();
    }

    const picker = document.getElementById('job-picker-map');
    const latitudeInput = document.getElementById('job-latitude');
    const longitudeInput = document.getElementById('job-longitude');
    const locationStatus = document.getElementById('job-location-status');
    if (picker && window.L && latitudeInput && longitudeInput) {
        const hasCoordinates = validCoordinate(latitudeInput.value, -90, 90)
            && validCoordinate(longitudeInput.value, -180, 180);
        const initialPoint = hasCoordinates
            ? [Number(latitudeInput.value), Number(longitudeInput.value)]
            : defaultCenter;
        const map = L.map(picker).setView(initialPoint, hasCoordinates ? 16 : 12);
        addBaseLayer(map, picker);
        let marker = hasCoordinates ? L.marker(initialPoint).addTo(map) : null;

        const setPoint = (latitude, longitude) => {
            latitudeInput.value = latitude.toFixed(7);
            longitudeInput.value = longitude.toFixed(7);
            marker?.remove();
            marker = L.marker([latitude, longitude]).addTo(map);
            locationStatus.textContent = 'Posizione selezionata.';
        };
        map.on('click', event => setPoint(event.latlng.lat, event.latlng.lng));

        document.getElementById('find-job-location')?.addEventListener('click', async () => {
            const city = document.querySelector('[name="citta"]')?.value?.trim();
            const address = document.querySelector('[name="indirizzo"]')?.value?.trim();
            const query = [address, city].filter(Boolean).join(', ');
            if (!query) {
                locationStatus.textContent = 'Inserisci almeno città o indirizzo.';
                return;
            }
            locationStatus.textContent = 'Ricerca posizione in corso...';
            try {
                const response = await fetch(`https://nominatim.openstreetmap.org/search?format=jsonv2&limit=1&q=${encodeURIComponent(query)}`, {
                    headers: { Accept: 'application/json' }
                });
                if (!response.ok) throw new Error('Geocoding failed');
                const results = await response.json();
                if (!results.length) {
                    locationStatus.textContent = 'Posizione non trovata. Puoi selezionarla cliccando sulla mappa.';
                    return;
                }
                const result = results[0];
                const latitude = Number(result.lat);
                const longitude = Number(result.lon);
                map.setView([latitude, longitude], 16);
                setPoint(latitude, longitude);
            } catch (error) {
                locationStatus.textContent = 'Impossibile cercare la posizione. Selezionala manualmente sulla mappa.';
            }
        });
    }

    function escapeHtml(value) {
        return String(value).replace(/[&<>"']/g, character => ({
            '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#039;'
        }[character]));
    }

    // Filtri per la lista lavori: ogni card resta visibile solo se rispetta tutti i criteri.
    const category = document.getElementById('filter-category');
    const distance = document.getElementById('filter-distance');
    const time = document.getElementById('filter-time');
    const pay = document.getElementById('filter-pay');
    const clear = document.getElementById('clear-filters');
    const cards = Array.from(document.querySelectorAll('.job-card'));

    if (cards.length) {
        const applyFilters = () => {
            cards.forEach(card => {
                const cardCategory = card.dataset.category || '';
                const parsedDistance = Number(card.dataset.distance);
                const cardDistance = Number.isFinite(parsedDistance) ? parsedDistance : 0;
                const cardTime = card.dataset.time || '';
                const parsedPay = Number(card.dataset.pay);
                const cardPay = Number.isFinite(parsedPay) ? parsedPay : 0;

                const categoryOk = !category?.value || category.value === cardCategory;
                const distanceOk = !distance?.value || cardDistance <= Number(distance.value);
                const timeOk = !time?.value || !cardTime || cardTime <= time.value;
                const payOk = !pay?.value || cardPay >= Number(pay.value);

                card.style.display = (categoryOk && distanceOk && timeOk && payOk) ? '' : 'none';
            });
        };

        [category, distance, time, pay].forEach(el => {
            el?.addEventListener('input', applyFilters);
            el?.addEventListener('change', applyFilters);
        });
        clear?.addEventListener('click', () => {
            // Reset completo dei controlli e riesecuzione del filtro.
            if (category) category.value = '';
            if (distance) distance.value = '';
            if (time) time.value = '';
            if (pay) pay.value = '';
            applyFilters();
        });
    }

    const youngCity = document.getElementById('filter-young-city');
    const youngSkill = document.getElementById('filter-young-skill');
    const youngAge = document.getElementById('filter-young-age');
    const youngRating = document.getElementById('filter-young-rating');
    const clearYoung = document.getElementById('clear-young-filters');
    const youngCards = Array.from(document.querySelectorAll('.young-card'));

    const applyYoungFilters = () => {
        youngCards.forEach(card => {
            const cardCity = card.dataset.city || '';
            const cardSkills = card.dataset.skills || '';
            const cardAge = Number(card.dataset.age || 0);
            const cardRating = Number(card.dataset.rating || 0);

            const cityValue = (youngCity?.value || '').trim().toLowerCase();
            const skillValue = (youngSkill?.value || '').trim().toLowerCase();
            const ageValue = Number(youngAge?.value || 0);
            const ratingValue = Number(youngRating?.value || 0);

            const cityOk = !cityValue || cardCity.includes(cityValue);
            const skillOk = !skillValue || cardSkills.includes(skillValue);
            const ageOk = !youngAge?.value || cardAge <= ageValue;
            const ratingOk = !youngRating?.value || cardRating >= ratingValue;

            card.style.display = (cityOk && skillOk && ageOk && ratingOk) ? '' : 'none';
        });
    };

    [youngCity, youngSkill, youngAge, youngRating].forEach(el => {
        el?.addEventListener('input', applyYoungFilters);
        el?.addEventListener('change', applyYoungFilters);
    });
    clearYoung?.addEventListener('click', () => {
        // Stesso pattern del filtro lavori, applicato all'elenco giovani.
        if (youngCity) youngCity.value = '';
        if (youngSkill) youngSkill.value = '';
        if (youngAge) youngAge.value = '';
        if (youngRating) youngRating.value = '';
        applyYoungFilters();
    });
});
