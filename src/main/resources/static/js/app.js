document.addEventListener('DOMContentLoaded', () => {
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
