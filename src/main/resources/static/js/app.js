document.addEventListener('DOMContentLoaded', () => {
    const category = document.getElementById('filter-category');
    const distance = document.getElementById('filter-distance');
    const time = document.getElementById('filter-time');
    const pay = document.getElementById('filter-pay');
    const clear = document.getElementById('clear-filters');
    const cards = Array.from(document.querySelectorAll('.job-card'));

    if (!cards.length) return;

    const applyFilters = () => {
        cards.forEach(card => {
            const c = card.dataset.category;
            const d = Number(card.dataset.distance);
            const t = card.dataset.time;
            const p = Number(card.dataset.pay);

            const categoryOk = !category?.value || category.value === c;
            const distanceOk = !distance?.value || d <= Number(distance.value);
            const timeOk = !time?.value || t <= time.value;
            const payOk = !pay?.value || p >= Number(pay.value);

            card.style.display = (categoryOk && distanceOk && timeOk && payOk) ? '' : 'none';
        });
    };

    [category, distance, time, pay].forEach(el => el?.addEventListener('input', applyFilters));
    clear?.addEventListener('click', () => {
        if (category) category.value = '';
        if (distance) distance.value = '';
        if (time) time.value = '';
        if (pay) pay.value = '';
        applyFilters();
    });
});
